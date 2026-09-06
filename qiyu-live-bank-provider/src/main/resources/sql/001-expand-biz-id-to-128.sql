-- ============================================================
-- 送礼业务号跨用户防碰撞：biz_id 从 VARCHAR(64) 扩展到 VARCHAR(128)
--
-- 原因：
--   当前 bizId = requestId（前端生成），不同用户可能产生相同 requestId，
--   导致 t_qiyu_currency_trade 的 uk_qiyu_currency_trade_biz_type
--   唯一索引冲突。
--
--   改为 bizId = userId:requestId 格式后，最大长度：
--     userId (max 19) + ":" (1) + requestId (max 64) = 84 字符
--   VARCHAR(64) 无法容纳，需要扩展到 VARCHAR(128)。
--
-- 执行前确认：
--   SHOW COLUMNS FROM t_qiyu_currency_trade LIKE 'biz_id';
--   SHOW INDEX FROM t_qiyu_currency_trade WHERE Key_name = 'uk_qiyu_currency_trade_biz_type';
--
-- 执行后验证：
--   SHOW COLUMNS FROM t_qiyu_currency_trade LIKE 'biz_id';
--   -- 预期: biz_id VARCHAR(128) NOT NULL
-- ============================================================

ALTER TABLE t_qiyu_currency_trade
    MODIFY COLUMN biz_id VARCHAR(128) NOT NULL COMMENT '业务唯一号：格式为 userId:requestId，与 type 共同受唯一索引保护';
