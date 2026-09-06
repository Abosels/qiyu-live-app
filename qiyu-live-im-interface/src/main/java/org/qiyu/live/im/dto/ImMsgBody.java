package org.qiyu.live.im.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ImMsgBody implements Serializable {

    @Serial
    private static final long serialVersionUID = -16519841695491L;
    /**
     * 接入im服务的各个业务线id
     * [修复] 由基本类型 int 改为包装类型 Integer，与调用方的参数校验逻辑 (appId < 10000) 保持一致，避免拆箱时空指针风险
     */
    private Integer appId;
    /**
     * 用户ID
     * [修复] 由基本类型 long 改为包装类型 Long，与 ImContextUtils 和 LoginMsgHandler 中的类型保持一致
     */
    private Long userId;
    /**
     * 从业务服务中获取，用于在im服务建立连接的时候使用
     */
    private String token;
    /**
     * 业务标识
     */
    private int bizCode;
    /**
     * 和业务服务进行消息传递
     */
    private String data;
    /**
     * 唯一的消息ID
     */
    private String msgId;
    /**
     * 加入的房间id
     */
    private Integer roomId;
}
