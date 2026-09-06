package org.qiyu.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_gift_config")
public class GiftConfigPO {
    /**
     * 礼物唯一自增主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Integer giftId;

    /**
     * 礼物对应的虚拟货币售价
     */
    private Integer price;

    /**
     * 礼物展示名称
     */
    private String giftName;

    /**
     * 礼物状态：0 = 无效下架，1 = 有效上架
     */
    private Integer status;

    /**
     * 礼物静态封面图片网络地址
     */
    private String coverImgUrl;

    /**
     * 礼物动效 SVGA 资源文件地址
     */
    private String svgaUrl;

    /**
     * 礼物配置创建时间，默认取当前时间
     */
    private LocalDateTime createTime;

    /**
     * 礼物信息更新时间，数据修改时自动刷新
     */
    private LocalDateTime updateTime;
}
