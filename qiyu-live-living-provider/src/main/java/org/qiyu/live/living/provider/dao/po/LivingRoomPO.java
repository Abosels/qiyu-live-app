package org.qiyu.live.living.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("t_living_room")
@Data
public class LivingRoomPO {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 当前直播的主播id
     */
    private Long anchorId;
    private Integer type;
    private String roomName;
    private Integer status;
    private Integer watchNum;
    private Integer goodNum;
    private LocalDateTime startTime;
    private LocalDateTime updateTime;
}
