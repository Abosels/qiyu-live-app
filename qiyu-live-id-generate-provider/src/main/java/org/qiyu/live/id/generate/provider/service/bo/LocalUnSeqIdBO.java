package org.qiyu.live.id.generate.provider.service.bo;

import lombok.Data;

import java.util.concurrent.ConcurrentLinkedQueue;

@Data
public class LocalUnSeqIdBO {
    private long id;
    /**
     * 设计一个并发的队列，存放无序id，用于后续取用
     */
    private ConcurrentLinkedQueue<Long> unSeqIdQueue;
    private Long currentStart;
    private Long nextThreshold;
}
