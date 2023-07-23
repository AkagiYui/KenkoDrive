package com.akagiyui.drive.model.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册确认 请求体
 * @author AkagiYui
 */
@Data
public class RegisterConfirmRequest {
    /**
     * 邮箱
     */
    @Email(message = "Email format is incorrect")
    @NotBlank(message = "Email cannot be empty")
    private String email;
    /**
     * 验证码
     */
    @NotBlank(message = "Verify code cannot be empty")
    private String verifyCode;
}
