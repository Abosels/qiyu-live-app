package provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_pay_topic")
public class PayTopicPO {
    @TableId(type = IdType.AUTO)
    private Long id;
    /** MQ主题名称 */
    private String topic;
    /** 业务编码 */
    private Integer bizCode;
    /** 描述 */
    private String remark;
    /**
     * 是否有效
     */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
