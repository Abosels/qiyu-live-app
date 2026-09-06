-- 订单金额在预下单时固化，支付时不再受商品后续改价影响。
ALTER TABLE t_sku_order_info
    ADD COLUMN total_price INT NOT NULL DEFAULT 0 COMMENT '下单时的虚拟币总价快照' AFTER sku_id_list;
