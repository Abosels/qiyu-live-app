package org.qiyu.live.user.interfaces.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * DTO = 用来在接口 / RPC / 服务之间传输数据的 Java 对象
 *
 * UserPO：用户服务内部查数据库用
 * UserDTO：用户服务对外传输用
 *
 * 你之前的 RPC 接口是：
 * UserDTO getUserById(Long userId);
 * boolean updateUserInfo(UserDTO userDTO);
 * Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList);
 * 这说明 UserDTO 是用户服务对外暴露的数据对象。
 *
 * 尤其你这里用了 Dubbo RPC，不只是网页会用 DTO，其他微服务远程调用用户服务时，也会用 DTO。
 * 比如：
 * live-api 服务
 *   ↓ Dubbo RPC
 * user-provider 服务
 * 中间传输的用户数据就是 UserDTO。
 *
 */
@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4479676188893306220L;

    private Long userId;
    private String nickName;
    private String trueName;
    private String avatar;
    private Integer sex;
    private Integer workCity;
    private Integer bornCity;
    private Date bornDate;
    private Date createTime;
    private Date updateTime;


}
