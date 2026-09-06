package provider.service;

import org.qiyu.live.bank.dto.PayProductDTO;

import java.util.List;

public interface IPayProductService {
    /**
     * 返回批量商品信息
     *
     * @param type 不同的业务场景所使用的产品
     */
    List<PayProductDTO> products(Integer type);
    /**
     * 根据产品id查询
     * @param productId
     * @return
     */
    PayProductDTO getByProductId(Integer productId);
}
