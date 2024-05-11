package com.akagiyui.drive.service.impl

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.notifier.AliyunSmsCodePusher
import com.akagiyui.drive.service.SettingService
import com.akagiyui.drive.service.SmsService
import org.springframework.stereotype.Service

/**
 * 短信 Service 实现类
 * @author AkagiYui
 */
@Service
class SmsServiceImpl(
    private val settingService: SettingService,
) : SmsService {

    private val log by LoggerDelegate()

    override fun sendSmsOneTimePassword(phone: String, otp: String) {
        val pusher = AliyunSmsCodePusher(
            settingService.aliyunSmsAccessKeyId,
            settingService.aliyunSmsAccessKeySecret,
            settingService.aliyunSmsSignName,
            settingService.aliyunSmsTemplateCode
        )
        pusher.sendSms(phone, mapOf("code" to otp))
        log.info("send sms to $phone, otp: $otp")
    }

}
