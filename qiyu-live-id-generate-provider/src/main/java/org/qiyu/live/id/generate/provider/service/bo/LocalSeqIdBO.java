package org.qiyu.live.id.generate.provider.service.bo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

@Data
public class LocalSeqIdBO {

    private long id;
    /**
     * 在内存中记录的当前有序id的值
     * 多线程利用原子类来保证线程安全
     */
    private AtomicLong currentNum;

    private Long currentStart;
    private Long nextThreshold;

}
