package com.akagiyui.drive.service.impl

import com.akagiyui.drive.service.MailService
import jakarta.mail.MessagingException
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
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
    private val javaMailSender: JavaMailSender,
    private val templateEngine: TemplateEngine,
    @Value("\${application.email.from}")
    private val sendFrom: String,
) : MailService {

    override fun sendEmailVerifyCode(email: String, verifyCode: String, timeout: Long) {
        val subject = "Kenko Drive 邮箱验证码"
        val mimeMessage: MimeMessage = javaMailSender.createMimeMessage()
        try {
            val mimeMessageHelper = MimeMessageHelper(mimeMessage, true).apply {
                setFrom(sendFrom)
                setTo(email)
                setSubject(subject)
            }

            // 构建邮件内容
            val context = Context().apply {
                setVariable("code", verifyCode)
                setVariable("timeout", timeout)
            }
            val content = templateEngine.process("mail_verify", context)
            mimeMessageHelper.setText(content, true)

            javaMailSender.send(mimeMessage)
        } catch (e: MessagingException) {
            throw RuntimeException(e)
        }
    }
}
