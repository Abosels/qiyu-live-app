package org.qiyu.live.gift.constants;

public enum SkuOrderInfoEnum {
    PREPARE_PAY(0,"待支付状态"),
    HAS_PAY(1,"已支付状态"),
    CANCELLED(2, "超时取消"),
    END(3, "订单已关闭"),
    /** 已抢占支付权并正在扣币，避免支付与超时回滚并发处理同一订单。 */
    PAYING(4, "支付处理中");

    Integer code;
    String desc;

    SkuOrderInfoEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
