package org.qiyu.live.gift.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.qiyu.live.gift.dto.GiftRecordDTO;
import org.qiyu.live.gift.interfaces.IGiftRecordRpc;
import org.qiyu.live.gift.provider.service.IGiftRecordService;
import jakarta.annotation.Resource;

@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {

    @Resource
    private IGiftRecordService giftRecordService;
    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        giftRecordService.insertOne(giftRecordDTO);
    }
}
