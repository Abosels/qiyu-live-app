package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_red_packet_config")
public class RedPacketConfigPO {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 主播id
     */
    private Integer anchorId;

    /**
     * 红包雨活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 红包雨活动结束后，一共领取数量
     */
    private Integer totalGet;

    /**
     * 红包雨活动结束后，一共领取金额
     */
    private Integer totalGetPrice;

    /**
     * 最大领取金额
     */
    private Integer maxGetPrice;

    /**
     * (0无效 1有效)
     */
    private Integer status;

    /**
     * 红包雨总金额数
     */
    private Integer totalPrice;

    /**
     * 红包雨总红包数
     */
    private Integer totalCount;

    /**
     * 唯一code
     */
    private String configCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
