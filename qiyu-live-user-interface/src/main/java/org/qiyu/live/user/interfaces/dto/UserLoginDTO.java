package org.qiyu.live.user.interfaces.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 156456L;

    private long userId;
    private String phone;
    private String token;
    private boolean isLoginSuccess;
    private String desc;

    public static UserLoginDTO loginError(String desc){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setLoginSuccess(false);
        userLoginDTO.setDesc(desc);
        return userLoginDTO;
    }

    public static UserLoginDTO loginSuccess(long userId, String phone, String token){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUserId(userId);
        userLoginDTO.setLoginSuccess(true);
        userLoginDTO.setPhone(phone);
        userLoginDTO.setToken(token);
        return userLoginDTO;
    }

}
