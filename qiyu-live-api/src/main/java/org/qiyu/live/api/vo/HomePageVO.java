package org.qiyu.live.api.vo;

import lombok.Data;

import java.util.Date;

@Data
public class HomePageVO {
    boolean loginStatus;
    private Long userId;
    private String nickName;
    private String avatar;
    /**
     * 当前用户是否展示“开播”按钮。
     * 这个字段由会员/标签服务聚合后返回，前端只负责展示。
     */
    private boolean showStartLivingBtn;
    private Date createTime;
    private Date updateTime;
}
