package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.GiftConfigDTO;

import java.util.List;

/**
 *
 * 礼物接口
 */
public interface IGiftConfigRpc {
    /**
     * 按照礼物id查询
     * @param giftId
     * @return
     */
    GiftConfigDTO getGiftId(Integer giftId);

    /**
     * 查询所有礼物信息
     * @return
     */
    List<GiftConfigDTO> queryGiftList();

    void insertGift(GiftConfigDTO giftDTO);

    void updateGift(GiftConfigDTO giftDTO);
}
