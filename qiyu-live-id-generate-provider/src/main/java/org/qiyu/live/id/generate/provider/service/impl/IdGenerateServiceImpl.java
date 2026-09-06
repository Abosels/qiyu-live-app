package org.qiyu.live.id.generate.provider.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.qiyu.live.id.generate.provider.dao.mapper.IdGenerateMapper;
import org.qiyu.live.id.generate.provider.dao.po.IdGeneratePO;
import org.qiyu.live.id.generate.provider.service.IdGenerateService;
import org.qiyu.live.id.generate.provider.service.bo.LocalSeqIdBO;
import org.qiyu.live.id.generate.provider.service.bo.LocalUnSeqIdBO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class IdGenerateServiceImpl implements IdGenerateService , InitializingBean {

    @Autowired
    private IdGenerateMapper idGenerateMapper;

    private static final Map<Integer, LocalSeqIdBO> localSeqIdMap = new ConcurrentHashMap<>();
    private static final Map<Integer, LocalUnSeqIdBO> localUnSeqIdMap = new ConcurrentHashMap<>();
    private static final float UPDATE_RATE = 0.75f;
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(8, 16, 3, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1000),
            new ThreadFactory(){
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("id-generate-thread-" + ThreadLocalRandom.current().nextInt(1000));
                    return thread;
                }
            });
    /**
     *     java并发包专门做限流的组件
     */
    private static Map<Integer,Semaphore> semaphoreMap = new ConcurrentHashMap<>();

    @Override
    public Long getSeqId(Integer id) {
        if (id == null){
            log.error("id is null, id is {}",id);
            return null;
        }
        LocalSeqIdBO localSeqIdBO = localSeqIdMap.get(id);
        if (localSeqIdBO == null){
            log.error("localSeqIdBO is null, id is {}",id);
            return null;
        }
        this.refreshLocalSeqIdBO(localSeqIdBO);
        if(localSeqIdBO.getCurrentNum().get() > localSeqIdBO.getNextThreshold()){
            log.error("id段已用完，请勿重复使用，id is {}",id);
            return null;
        }
        long returnId = localSeqIdBO.getCurrentNum().getAndIncrement();
        return returnId;
    }

    @Override
    public Long getUnSeqId(Integer id) {
        log.info("getUnSeqId 被调用, id={}, localUnSeqIdMap.size={}, localSeqIdMap.size={}",
                id, localUnSeqIdMap.size(), localSeqIdMap.size());
        if(id == null){
            log.error("id is null, id is {}",id);
            return null;
        }
        LocalUnSeqIdBO localUnSeqIdBO = localUnSeqIdMap.get(id);
        if (localUnSeqIdBO == null){
            log.error("localUnSeqIdBO is null, id={}, map中存在的key={}", id, localUnSeqIdMap.keySet());
            return null;
        }
        this.refreshLocalUnSeqIdBO(localUnSeqIdBO);
        int queueSize = localUnSeqIdBO.getUnSeqIdQueue().size();
        if (queueSize == 0){
            log.error("无序id段已用完，请勿重复使用，id is {}",id);
            return null;
        }
        // 从队列中弹出一个无序id
        long returnId = localUnSeqIdBO.getUnSeqIdQueue().poll();
        log.info("getUnSeqId 成功返回, id={}, returnId={}, 队列剩余={}", id, returnId, queueSize - 1);
        return returnId;
    }

    /**
     * @throws Exception
     *
     * 在SpringBoot服务启动的时候，Bean在启动的生命周期回调这个函数
     * 初始化我们的有序Map ：localSeqIdMap
     *
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<IdGeneratePO> idGeneratePOList = idGenerateMapper.selectAll();
        for(IdGeneratePO idGeneratePO : idGeneratePOList){

            tryUpdateMySQLRecord(idGeneratePO);
            /**
             * 每次更新只能有一个线程进来
             * 给这个 idGeneratePO.getId() 创建一个 Semaphore(1)
             * 放入 semaphoreMap
             */
            semaphoreMap.put(idGeneratePO.getId(),new Semaphore(1));

        }
    }

    /**
     * 同步执行,很多网络IO,性能较差
     * 更新MYSQL中的分布式id的配置信息，占用相应的id段。
     * @param idGeneratePO
     */
    private void tryUpdateMySQLRecord(IdGeneratePO idGeneratePO){
        //可能是多进程进行更新，所以更新失败要进行重试
        int updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
        if (updateResult > 0){
            localIdBOHandler(idGeneratePO);
            return;
        }
        for (int i = 0; i < 3; i++){
            //重新查询一下
            idGeneratePO = idGenerateMapper.selectById(idGeneratePO.getId());
            updateResult = idGenerateMapper.updateNewIdCountAndVersion(idGeneratePO.getId(), idGeneratePO.getVersion());
            if (updateResult > 0){
                localIdBOHandler(idGeneratePO);
                return;
            }
        }
        log.error("表ID段竞争激烈，占用更新失败，idGeneratePO.getId is {}",idGeneratePO.getId());
    }

    /**
     * 刷新缓存中的LocalSeqIdBO，保证缓存中的id没有呗用完。
     * 占用了75%的id数就刷新一次
     * @param localSeqIdBO
     */
    private void refreshLocalSeqIdBO(LocalSeqIdBO localSeqIdBO) {
        long currentStart = localSeqIdBO.getCurrentStart();
        long nextThreshold = localSeqIdBO.getNextThreshold();
        long currentNum = localSeqIdBO.getCurrentNum().get();
        long step = nextThreshold - currentStart;
        if (currentNum - currentStart > step * UPDATE_RATE  ) {
            Semaphore semaphore = semaphoreMap.get(localSeqIdBO.getId());
            if (semaphore == null){
                log.error("semaphore is null, id is {}",localSeqIdBO.getId());
                return;
            }
            boolean tryAcquireStatus = semaphore.tryAcquire(); // Try to acquire the semaphore
            if (tryAcquireStatus) {
                log.info("获取到锁,尝试开始同步，id is {}",localSeqIdBO.getId());
                //异步执行同步更新id段的操作
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localSeqIdBO.getId());
                            tryUpdateMySQLRecord(idGeneratePO);
                        } catch (Exception e) {
                            log.error("同步更新失败，idGeneratePO.getId is {}",localSeqIdBO.getId());
                        }finally {
                            log.info("异步更新成功，idGeneratePO.getId is {}",localSeqIdBO.getId());
                            semaphoreMap.get(localSeqIdBO.getId()).release();
                        }
                    }
                });
            }
        }
    }

    /**
     * 刷新缓存中的LocalUnSeqIdBO，保证缓存中的id没有呗用完。
     * 占用了75%的id数就刷新一次
     * @param localUnSeqIdBO
     */
    private void refreshLocalUnSeqIdBO(LocalUnSeqIdBO localUnSeqIdBO) {
        long begin = localUnSeqIdBO.getCurrentStart();
        long end = localUnSeqIdBO.getNextThreshold();
        long remainSize = localUnSeqIdBO.getUnSeqIdQueue().size();
        if ( end - begin > remainSize * UPDATE_RATE  ) {
            Semaphore semaphore = semaphoreMap.get(localUnSeqIdBO.getId());
            if (semaphore == null){
                log.error("semaphore is null, id is {}",localUnSeqIdBO.getId());
                return;
            }
            boolean tryAcquireStatus = semaphore.tryAcquire(); // Try to acquire the semaphore
            if (tryAcquireStatus) {
                log.info("获取到锁,尝试开始同步，id is {}",localUnSeqIdBO.getId());
                //异步执行同步更新id段的操作
                threadPoolExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            IdGeneratePO idGeneratePO = idGenerateMapper.selectById(localUnSeqIdBO.getId());
                            tryUpdateMySQLRecord(idGeneratePO);
                        }catch (Exception e){
                            log.error("同步更新失败，idGeneratePO.getId is {}",localUnSeqIdBO.getId());
                        }finally {
                            log.info("异步更新成功，idGeneratePO.getId is {}",localUnSeqIdBO.getId());
                            semaphoreMap.get(localUnSeqIdBO.getId()).release();
                        }
                    }
                });
            }
        }
    }
    /**
     * 封装获取无序id 和 有序id 以及重试三次获取id
     */
    private void localIdBOHandler(IdGeneratePO idGeneratePO){
        long currentStart = idGeneratePO.getCurrentStart();
        long nextThreshold = idGeneratePO.getNextThreshold();
        long currentNum = currentStart;
        if(idGeneratePO.getIsSeq() == 1){
            LocalSeqIdBO localSeqIdBO = new LocalSeqIdBO();
            AtomicLong atomicLong = new AtomicLong(currentNum);
            localSeqIdBO.setId(idGeneratePO.getId());
            localSeqIdBO.setCurrentNum(atomicLong);
            localSeqIdBO.setCurrentStart(currentStart);
            localSeqIdBO.setNextThreshold(nextThreshold);
            localSeqIdMap.put(idGeneratePO.getId(),localSeqIdBO);
            log.info("更新成功，idGeneratePO is {}",idGeneratePO);
        }else {
            LocalUnSeqIdBO localUnSeqIdBO = new LocalUnSeqIdBO();
            localUnSeqIdBO.setId(idGeneratePO.getId());
            localUnSeqIdBO.setCurrentStart(currentStart);
            localUnSeqIdBO.setNextThreshold(nextThreshold);
            long begin = localUnSeqIdBO.getCurrentStart();
            long end = localUnSeqIdBO.getNextThreshold();
            List<Long> idList = new ArrayList<>();
            for (long i = begin; i < end; i++){
                idList.add(i);
            }
            //洗牌算法 将本地的id打乱进行洗牌 放入到队列中
            Collections.shuffle(idList);
            ConcurrentLinkedQueue unSeqIdQueue = new ConcurrentLinkedQueue<>();
            unSeqIdQueue.addAll(idList);
            localUnSeqIdBO.setUnSeqIdQueue(unSeqIdQueue);
            localUnSeqIdMap.put(idGeneratePO.getId(),localUnSeqIdBO);
            log.info("更新成功，idGeneratePO.getId is {}",idGeneratePO.getId());
        }
    }
}
