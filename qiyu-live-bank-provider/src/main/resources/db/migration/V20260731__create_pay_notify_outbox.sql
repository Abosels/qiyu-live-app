CREATE TABLE IF NOT EXISTS t_pay_notify_outbox (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    biz_key VARCHAR(64) NOT NULL COMMENT '支付订单号',
    topic VARCHAR(255) NOT NULL COMMENT 'RocketMQ Topic',
    message_body TEXT NOT NULL COMMENT '消息 JSON',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0待发送，1发送中，2已发送',
    retry_count INT NOT NULL DEFAULT 0 COMMENT '失败重试次数',
    next_retry_time DATETIME NOT NULL COMMENT '下次允许发送时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pay_notify_outbox_biz_key (biz_key),
    KEY idx_pay_notify_outbox_dispatch (status, next_retry_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付成功事件本地消息表';
