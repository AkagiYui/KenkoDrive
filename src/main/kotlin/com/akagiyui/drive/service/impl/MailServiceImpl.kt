package com.akagiyui.drive.service.impl

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.service.MailService
import com.akagiyui.drive.service.SettingService
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

/**
 * 邮件服务 实现
 *
 * @author AkagiYui
 */
@Service
class MailServiceImpl(
    private val javaMailSender: JavaMailSenderImpl,
    private val templateEngine: TemplateEngine,
    private val settingService: SettingService,
    @Value("\${application.name}")
    private val appName: String,
) : MailService {
    private val log by LoggerDelegate()

    private fun updateMailConfig() {
        javaMailSender.host = settingService.smtpHost
        javaMailSender.port = settingService.smtpPort
        javaMailSender.username = settingService.smtpUsername
        javaMailSender.password = settingService.smtpPassword
        javaMailSender.defaultEncoding = "UTF-8"
        javaMailSender.protocol = "smtp"

        val properties = javaMailSender.javaMailProperties
        properties.setProperty("mail.smtp.ssl.enable", settingService.smtpSsl.toString())
        properties.setProperty("mail.smtp.auth", "true")
    }

    override fun sendEmailVerifyCode(email: String, verifyCode: String, timeout: Long) {
        updateMailConfig()
        val subject = "$appName 邮箱验证码"
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true).apply {
                setFrom("$appName<${settingService.mailFrom}>")
                setTo(email)
                setSubject(subject)
            }

            // 构建邮件内容
            val context = Context().apply {
                setVariable("appName", appName)
                setVariable("code", verifyCode)
                setVariable("timeout", timeout)
            }
            val content = templateEngine.process("mail_verify", context)
            mimeMessageHelper.setText(content, true)

            javaMailSender.send(mimeMessage)
        } catch (e: MessagingException) {
            log.error("Send email verify code failed", e)
        }
    }
}
