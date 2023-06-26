package com.akagiyui.drive.entity.response;

import com.akagiyui.drive.entity.User;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用户信息响应
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class UserInfoResponse {
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 已禁用
     */
    private Boolean disabled;

    /**
     * 从用户实体转换
     */
    public static UserInfoResponse fromUser(User user) {
        return new UserInfoResponse()
                .setUsername(user.getUsername())
                .setNickname(user.getNickname())
                .setEmail(user.getEmail())
                .setDisabled(user.getDisabled());
    }
}
