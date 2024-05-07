package com.akagiyui.drive.component.captcha

import com.akagiyui.common.GeetestCaptchaV4Template
import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.exception.GeetestCaptchaValidateException
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * 极验验证码切面
 * @author AkagiYui
 */
@Aspect
@Component
class GeetestCaptchaV4Aspect(
    @Value("\${application.captcha.geetest.id}") private val captchaId: String,
    @Value("\${application.captcha.geetest.key}") private val captchaKey: String,
) {
    private val log by LoggerDelegate()
    private val geetestCaptchaV4Template = GeetestCaptchaV4Template(captchaId, captchaKey)

    @Around("@annotation(annotation)")
    fun aroundAdvice(joinPoint: ProceedingJoinPoint, annotation: GeetestCaptchaV4Protected): Any? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        try {
            geetestCaptchaV4Template.validate(
                request.getParameter("lot_number") ?: throw CustomException(ResponseEnum.INVALID_CAPTCHA),
                request.getParameter("captcha_output") ?: throw CustomException(ResponseEnum.INVALID_CAPTCHA),
                request.getParameter("pass_token") ?: throw CustomException(ResponseEnum.INVALID_CAPTCHA),
                request.getParameter("gen_time") ?: throw CustomException(ResponseEnum.INVALID_CAPTCHA),
            ).apply {
                log.debug("Geetest CAPTCHA Validate: {}", this)
            }
        } catch (e: GeetestCaptchaValidateException) {
            log.error("Geetest CAPTCHA Validate Error: {}", e.message)
            throw CustomException(ResponseEnum.INVALID_CAPTCHA)
        }
        return joinPoint.proceed()
    }

}
