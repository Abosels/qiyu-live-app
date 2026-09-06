package org.qiyu.live.msg.provider.config;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    // 业务通用异步线程池，供短信发送、延迟任务等场景使用
    public static final ThreadPoolExecutor commonAsyncPool = new ThreadPoolExecutor(
            2,
            8,
            3,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                private final AtomicInteger threadIndex = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("qiyu-common-async-" + threadIndex.getAndIncrement());
                    thread.setDaemon(false);
                    return thread;
                }
            }
    );
}
