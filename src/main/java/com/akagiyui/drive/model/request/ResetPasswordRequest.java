package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 重置密码 请求体
 *
 * @author AkagiYui
 */
@Data
public class ResetPasswordRequest {
    /**
     * 新密码
     * todo 补充校验信息
     */
    @NotNull()
    @NotBlank()
    private String newPassword;
}
