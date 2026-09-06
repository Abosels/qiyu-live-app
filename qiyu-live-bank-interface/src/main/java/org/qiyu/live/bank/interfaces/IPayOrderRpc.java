package org.qiyu.live.bank.interfaces;

import org.qiyu.live.bank.dto.PayOrderDTO;

public interface IPayOrderRpc {
    /**
     * 插入订单
     *
     * @param payOrderDTO
     * @return
     */
    String insertOne(PayOrderDTO payOrderDTO);

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
     * 支付宝创建收银台表单前查询订单快照。
     *
     * @param orderId 商户订单号
     * @return 订单不存在时返回 null
     */
    PayOrderDTO queryByOrderId(String orderId);
}
