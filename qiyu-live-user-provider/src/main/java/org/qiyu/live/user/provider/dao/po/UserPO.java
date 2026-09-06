package org.qiyu.live.user.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * PO = 和数据库表结构绑定的 Java 对象
 * PO 主要是给 Mapper / DAO 层 用的。
 * 数据库返回的是一行行记录
 * MyBatis-Plus 把这些记录封装成 UserPO 对象
 *
 * 为什么 ServiceImpl 里要 PO 转 DTO？ 因为 UserPO 是数据库层对象，不应该直接暴露给外面。
 *                                   1.保护数据库结构，假设以后数据库表多了字段：比如password 不应该返回给前端，也不应该暴露给其他服务
 *                                   2：对外接口要稳定数据库字段可能会变。比如数据库里叫：nick_name   Java PO 里叫：nickname以后数据库可能拆表、改字段、换存储方式。但对外接口的 UserDTO 可以保持不变。
 *                                   3：DTO 可以只放外部需要的字段 不返回password IdCard 等 关键隐私信息
 * 前端 JSON
 *    ↓ Spring MVC 自动转换
 * Controller 参数 DTO / VO
 *    ↓
 * Service 层处理 DTO
 *    ↓
 * 转换成 PO
 *    ↓
 * Mapper 操作数据库
 *    ↓
 * 数据库 user 表
 *
 * 查询数据库时就反过来
 */

@TableName("t_user")
@Data
public class UserPO {
    // 主键
    @TableId(type = IdType.INPUT)
    private Long userId;
    // 告诉java里的nickname 在数据库叫做 nick_name
    @TableField("nick_name")
    private String nickname;
    @TableField("true_name")
    private String trueName;
    private String avatar;
    private Integer sex;
    @TableField("work_city")
    private Integer workCity;
    @TableField("born_city")
    private Integer bornCity;
    @TableField("born_date")
    private Date bornDate;
    @TableField("create_time")
    private Date createTime;
    @TableField("update_time")
    private Date updateTime;
}
