package com.akagiyui.drive.model.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @Email(message = "{email format is incorrect}")
    @NotNull(message = "{email is missing}")
    @NotBlank(message = "{email cannot be empty}")
    private String email;
    /**
     * 验证码
     */
    @NotBlank(message = "{verifyCode cannot be empty}")
    @NotNull(message = "{verifyCode is missing}")
    private String verifyCode;
}
