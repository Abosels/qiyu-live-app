package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftRecordDTO;

public interface IGiftRecordRpc {
    /**
     * 插入单个礼物信息
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
