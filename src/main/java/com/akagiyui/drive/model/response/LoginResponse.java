package com.akagiyui.drive.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录响应
 * @author AkagiYui
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    /**
     * token
     */
    private String token;
    /**
     * 刷新token
     */
    private String refreshToken; // TODO 刷新token
}
