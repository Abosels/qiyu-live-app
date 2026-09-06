package org.qiyu.live.api.vo.req;

import lombok.Data;

@Data
public class OnlinePKReqVO {
    private Integer roomId;
    /** 主播用户 ID（服务端从 RequestContext 注入，不可信任前端） */
    private Long anchorId;
}
