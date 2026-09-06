package org.qiyu.live.user.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.idea.qiyu.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.qiyu.live.common.interfaces.utils.ConvertBeanUtils;
import org.qiyu.live.user.interfaces.dto.UserDTO;
import org.qiyu.live.user.provider.dao.mapper.IUserMapper;
import org.qiyu.live.user.provider.dao.po.UserPO;
import org.qiyu.live.user.provider.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class UserServiceImpl implements IUserService {
    @Resource
    private IUserMapper userMapper;

    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private MQProducer mqProducer;

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null) {
            return null;
        }

        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if (userDTO != null) {
            return userDTO;
        }

        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if (userDTO != null) {
            redisTemplate.opsForValue().set(key, userDTO, 30, TimeUnit.MINUTES);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        // 1. 基础参数校验：对象为空、userId 为空或非法时直接返回失败
        if (userDTO == null || userDTO.getUserId() == null || userDTO.getUserId() <= 0) {
            return false;
        }

        // 2. DTO -> PO，复用通用转换工具，避免手写字段映射
        UserPO userPO = ConvertBeanUtils.convert(userDTO, UserPO.class);
        if (userPO == null) {
            return false;
        }

        // 3. 设置更新时间，确保每次资料变更都有最新时间戳
        userPO.setUpdateTime(new java.util.Date());

        // 4. 先更新数据库，保证最新数据已经落库
        userMapper.updateById(userPO);

        // 5. 第一次删除缓存，避免后续查询继续读到旧值,根据 userId 构造 Redis key，然后删除缓存。
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
        redisTemplate.delete(key);

        try {
            // 6. 意思是创建一条 RocketMQ 消息。构建延迟消息，把本次变更的用户信息发送给 RocketMQ
            Message message = new Message(
                    "user-update-cache",
                    JSON.toJSONString(userDTO).getBytes(StandardCharsets.UTF_8)
            );

            // 7. 设置延迟级别，1 代表消息会在稍后投递，用于第二次删缓存
            message.setDelayTimeLevel(1);

            // 8. 发送消息，交给消费者执行延迟双删中的“第二删”
            mqProducer.send(message);
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        } catch (RemotingException e) {
            throw new RuntimeException(e);
        } catch (MQBrokerException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // 9. 数据库更新、第一次删缓存、延迟消息发送都成功
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null){
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return new HashMap<>();
        }

        userIdList = userIdList.stream()
                // Do not silently discard local or early users whose ID is <= 10000.
                .filter(id -> id != null && id > 0)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return new HashMap<>();
        }

        List<String> keyList = new ArrayList<>();
        userIdList.forEach(userId -> keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId)));

        List<UserDTO> cacheResultList = redisTemplate.opsForValue().multiGet(keyList);
        List<UserDTO> userDTOList = cacheResultList == null
                ? new ArrayList<>()
                : cacheResultList.stream().filter(x -> x != null).collect(Collectors.toList());

        List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> userIdNotInCacheList = userIdList.stream()
                .filter(userId -> !userIdInCacheList.contains(userId))
                .collect(Collectors.toList());

        Map<Long, List<Long>> userIdMap = userIdNotInCacheList.stream()
                .collect(Collectors.groupingBy(userId -> userId % 100));

        List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
        userIdMap.values().parallelStream().forEach(queryUserIdList -> {
            List<UserDTO> convertResult = ConvertBeanUtils.convertList(
                    userMapper.selectBatchIds(queryUserIdList),
                    UserDTO.class
            );
            dbQueryResult.addAll(convertResult == null ? Collections.emptyList() : convertResult);
        });

        if (!CollectionUtils.isEmpty(dbQueryResult)) {
            Map<String, UserDTO> saveCacheMap = dbQueryResult.stream().collect(Collectors.toMap(
                    dto -> userProviderCacheKeyBuilder.buildUserInfoKey(dto.getUserId()),
                    dto -> dto,
                    (oldValue, newValue) -> newValue
            ));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K) redisKey, createRandomExpireTime(), TimeUnit.MINUTES);
                    }
                    return null;
                }
            });
        }

        userDTOList.addAll(dbQueryResult);
        return userDTOList.stream()
                .collect(Collectors.toMap(
                        UserDTO::getUserId,
                        dto -> dto,
                        (oldValue, newValue) -> newValue
                ));
    }

    private long createRandomExpireTime() {
        long time = ThreadLocalRandom.current().nextLong(1000);
        return time + 60 * 30;
    }
}
