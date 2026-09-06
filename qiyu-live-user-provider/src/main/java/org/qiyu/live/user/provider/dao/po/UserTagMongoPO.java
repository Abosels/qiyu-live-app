package org.qiyu.live.user.provider.dao.po;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

@Data
@Document(collection = "user_tag_record")
public class UserTagMongoPO {

    @Id
    private String id;

    // 用户ID唯一，防止插入重复文档
    private Long userId;

    // Java字段映射Mongo中的下划线字段名
    @Field("tag_info1")
    private Long tagInfo1;

    @Field("tag_info2")
    private Long tagInfo2;

    @Field("tag_info3")
    private Long tagInfo3;

    // 可读标签信息，例如 isVip=1
    private Map<String, Integer> tagInfo;

    private Long createTime;
    private Long updateTime;
}
