package com.akagiyui.drive.model.request;

import com.akagiyui.drive.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 添加用户 请求
 *
 * @author AkagiYui
 */
@Data
public class AddUserRequest {
    /**
     * 用户名
     */
    @NotBlank(message = "{username cannot be empty}")
    @NotNull(message = "{username is missing}")
    @Size(min = 3, max = 20, message = "{username length must be between 3 and 20}")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "{password cannot be empty}")
    @NotNull(message = "{password is missing}")
    @Size(min = 8, max = 64, message = "{password length must be more than 8}")
    private String password;

    /**
     * 昵称
     */
    @Size(max = 20, message = "{nickname length must be less than 20}")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "{email format is incorrect}")
    @NotBlank(message = "{email cannot be empty}")
    private String email;

    /**
     * 转换为用户实体
     */
    public User toUser() {
        return new User()
                .setUsername(username)
                .setPassword(password)
                .setNickname(nickname)
                .setEmail(email);
    }
}
