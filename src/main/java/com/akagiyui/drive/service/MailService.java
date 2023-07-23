package com.akagiyui.drive.service;

import org.springframework.scheduling.annotation.Async;

/**
 * 邮件 Service 接口
 * @author AkagiYui
 */
public interface MailService {
    /**
     * 发送邮件验证码
     * @param email 邮箱
     * @param verifyCode 验证码
     * @param timeout 验证码有效时间
     */
    @Async
    void sendEmailVerifyCode(String email, String verifyCode, Long timeout);
}
