package org.qiyu.live.bank.provider.service;

import org.qiyu.live.bank.dto.PayOrderDTO;
import org.qiyu.live.bank.provider.dao.po.PayOrderPO;

public interface IPayOrderService {
    /**
     * 插入订单
     *
     * @param payOrderPO
     * @return
     */
    String insertOne(PayOrderPO payOrderPO);

    /**
     * 根据主键id做更新
     * @param id
     * @param status
     * @return
     */
    boolean updateOrderStatus(Long id, Integer status);

    /**
     * 根据订单id做更新
     * @param orderId
     * @param status
     * @return
     */
    boolean updateOrderStatus(String orderId, Integer status);
    /**
     * 支付回调需要请求接口
     * @param payOrderDTO
     * @return
     */
    boolean payNotify(PayOrderDTO payOrderDTO);

    /**
     * 根据订单id查询
     *
     * @param orderId
     * @return
     */
    PayOrderPO queryByOrderId(String orderId);
}
