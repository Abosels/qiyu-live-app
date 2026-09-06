package org.qiyu.live.api.service;

import org.qiyu.live.api.vo.GiftConfigVO;
import org.qiyu.live.api.vo.req.GiftReqVO;

import java.util.List;

/**
 * 礼物相关 API 服务接口
 */
public interface IGiftService {

    /**
     * 查询所有礼物配置列表
     *
     * @return 礼物配置 VO 列表
     */
    List<GiftConfigVO> listGift();

    /**
     * 发送礼物
     * @param giftReqVO
     * @return
     */
    boolean sendGift(GiftReqVO giftReqVO);
}
