package org.qiyu.live.im.router.provider.service.impl;

import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.qiyu.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.qiyu.live.im.core.server.interfaces.contants.ImCoreServerConstants;
import org.qiyu.live.im.dto.ImMsgBody;
import org.qiyu.live.im.router.provider.service.ImRouterService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private IRouterHandlerRpc routerHandlerRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public boolean sendMsg(ImMsgBody imMsgBody) {
        //假设我们有100个userId -> 10台im服务器上 100个ip做分类->最终ip的数量一定是《=10
        String bindAddr = stringRedisTemplate.opsForValue().get(
                ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBody.getAppId() + ":" + imMsgBody.getUserId()
        );
        if (StringUtils.isEmpty(bindAddr)) {
            return false;
        }

        int separatorIndex = bindAddr.lastIndexOf("%");
        if (separatorIndex <= 0) {
            return false;
        }

        bindAddr = bindAddr.substring(0, separatorIndex);
        RpcContext.getContext().set("ip", bindAddr);
        routerHandlerRpc.sendMsg( imMsgBody );
        return true;
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        if (imMsgBodyList == null || imMsgBodyList.isEmpty()) {
            return;
        }
        List<Long> userIdList = imMsgBodyList.stream().map(ImMsgBody::getUserId).collect(Collectors.toList());
        //根据userId，将不同的userId的immsgbody分类存入map
        Map<Long,ImMsgBody> userIdMsgMap = imMsgBodyList.stream().collect(Collectors.toMap(ImMsgBody::getUserId, x -> x));
        //保证整个list集合的appId是同于一个
        if (imMsgBodyList == null || imMsgBodyList.isEmpty()) {
            return;
        }
        Integer appId = imMsgBodyList.get(0).getAppId();
        List<String> cacheKeyList = new ArrayList<>();
        userIdList.forEach(userId -> {
            String cacheKeyPrefix = ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId;
            cacheKeyList.add(cacheKeyPrefix);
        });
        //利用multiGet来批量获取
        List<String> ipList = stringRedisTemplate.opsForValue().multiGet(cacheKeyList);
        Map<String,List<Long>> userIpMap = new HashMap<>();
        for (int i = 0; i < ipList.size(); i++) {
            String bindAddr = ipList.get(i);
            if (bindAddr == null || bindAddr.isBlank()) {
                continue;
            }

            int separatorIndex = bindAddr.lastIndexOf("%");
            if (separatorIndex <= 0) {
                continue;
            }

            String currentIp = bindAddr.substring(0, bindAddr.lastIndexOf("%"));
            Long userId = userIdList.get(i);

            List<Long> currentUserIdList = userIpMap.get(currentIp);
            if (currentUserIdList == null) {
                currentUserIdList = new ArrayList<>();
            }

            currentUserIdList.add(userId);
            userIpMap.put(currentIp, currentUserIdList);
        }

        //将连接的同一台ip地址的imMsgBody组装到同一个list集合中，然后统一的发送
        for (String currentIp : userIpMap.keySet()) {
            RpcContext.getContext().set("ip", currentIp);
            List<Long> ipBindUserId = userIpMap.get(currentIp);
            List<ImMsgBody> batchSendMsgGroupByIpList = new ArrayList<>();
            for (Long userId : ipBindUserId) {
                ImMsgBody imMsgBody = userIdMsgMap.get(userId);
                batchSendMsgGroupByIpList.add(imMsgBody);
            }

            routerHandlerRpc.batchSendMsg( batchSendMsgGroupByIpList );
        }
    }
}
