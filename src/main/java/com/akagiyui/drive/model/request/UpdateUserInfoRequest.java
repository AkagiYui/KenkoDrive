package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改用户信息 请求
 *
 * @author AkagiYui
 */
@Data
public class UpdateUserInfoRequest {
    /**
     * 昵称
     */
    @Size(max = 20, message = "{nickname length must be less than 20}")
    private String nickname;

    /**
     * 邮箱
     */
    @Email(message = "{email format is incorrect}")
    private String email;
}
