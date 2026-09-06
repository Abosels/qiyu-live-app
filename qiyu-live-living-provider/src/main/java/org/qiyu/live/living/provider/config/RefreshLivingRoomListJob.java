package org.qiyu.live.living.provider.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.idea.qiyu.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.qiyu.live.living.interfaces.contants.LivingRoomTypeEnum;
import org.qiyu.live.living.interfaces.dto.LivingRoomRespDTO;
import org.qiyu.live.living.provider.service.ILivingRoomService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class RefreshLivingRoomListJob implements InitializingBean {

    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;

    //延迟任务
    private ScheduledThreadPoolExecutor schedulePool = new ScheduledThreadPoolExecutor(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        //一秒钟刷新一次直播间列表数据
        schedulePool.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000,1000, TimeUnit.MILLISECONDS);
    }
    class RefreshCacheListJob implements Runnable {

        @Override
        public void run() {
            String cacheKey = cacheKeyBuilder.buildLivingRoomListLock();
            //这把锁等他自动过期
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKey, 1, 1, TimeUnit.SECONDS);
            if (lockStatus) {
                log.info("[RefreshLivingRoomListJob] starting 加载db中记录的直播间进入redis里");
                refreshDBToRedis(LivingRoomTypeEnum.DEFAULT_LIVING_ROOM.getCode());
                refreshDBToRedis(LivingRoomTypeEnum.RK_LIVING_ROOM.getCode());
                log.info("[RefreshLivingRoomListJob] end 加载db中记录的直播间进入redis里");
            }
        }
    }
    private void refreshDBToRedis(Integer type) {
        String cacheKey = cacheKeyBuilder.buildLivingRoomList(type);
        List<LivingRoomRespDTO> resultList = livingRoomService.listAllLivingRoomFromDB(type);
        if (CollectionUtils.isEmpty(resultList)) {
            redisTemplate.delete(cacheKey);
            return;
        }
        String tempListName = cacheKey + "_temp";
        //按照查询出来的顺序，一个个地塞入到list集合中
        for (LivingRoomRespDTO livingRoomRespDTO : resultList) {
            redisTemplate.opsForList().rightPush(tempListName, livingRoomRespDTO);
        }
        //正在访问的list集合读取出来, del -> leftpush
        //直接修改重命名这个list，不要直接对原先地list进行修改，减少阻塞影响。
        redisTemplate.rename(tempListName, cacheKey);
        redisTemplate.delete(tempListName);
    }
}
