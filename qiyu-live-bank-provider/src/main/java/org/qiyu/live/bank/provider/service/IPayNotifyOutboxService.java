package org.qiyu.live.bank.provider.service;

/** 支付成功事件的本地消息表服务。 */
public interface IPayNotifyOutboxService {

    /** 在支付事务内创建一条待投递事件。 */
    void createPending(String topic, String messageBody, String bizKey);

    /** 定时投递待发送事件，失败记录留待后续重试。 */
    void dispatchPendingEvents();
}
