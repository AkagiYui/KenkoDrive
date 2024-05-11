package com.akagiyui.drive.service

import org.springframework.scheduling.annotation.Async

/**
 * 短信 Service 接口
 * @author AkagiYui
 */
interface SmsService {

    /**
     * 发送短信一次性密码
     * @param phone 手机号
     * @param otp 一次性密码
     */
    @Async
    fun sendSmsOneTimePassword(phone: String, otp: String)

}
