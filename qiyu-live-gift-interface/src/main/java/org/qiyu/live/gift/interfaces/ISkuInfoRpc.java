package org.qiyu.live.gift.interfaces;

import org.qiyu.live.gift.dto.SkuDetailInfoDTO;
import org.qiyu.live.gift.dto.SkuInfoDTO;

import java.util.List;

public interface ISkuInfoRpc {
    /**
     * 批量AnchorId查询
     * @param anchorId
     * @return
     */
    List<SkuInfoDTO> queryByAnchorId(Long anchorId);

    /**
     * 查询商品详情通过skuid
     * @param skuId
     * @return
     */
    SkuDetailInfoDTO queryBySkuId(Long skuId);

    /** 批量查询购物车中 SKU 的详情。 */
    List<SkuDetailInfoDTO> queryBySkuIds(List<Long> skuIdList);
}
