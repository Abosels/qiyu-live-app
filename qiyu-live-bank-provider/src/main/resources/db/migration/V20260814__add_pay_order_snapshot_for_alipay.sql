-- 支付宝异步回调只能以订单创建时的金额和金币数量为准，不能读取会变化的产品配置。
ALTER TABLE t_pay_order
    ADD COLUMN pay_amount INT NOT NULL DEFAULT 0 COMMENT '支付金额快照，单位分' AFTER source,
    ADD COLUMN coin_amount INT NOT NULL DEFAULT 0 COMMENT '金币数量快照' AFTER pay_amount,
    ADD COLUMN third_party_trade_no VARCHAR(64) NULL COMMENT '第三方支付交易号' AFTER coin_amount;

CREATE UNIQUE INDEX uk_pay_order_third_party_trade_no
    ON t_pay_order (third_party_trade_no);
