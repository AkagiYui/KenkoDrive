package com.akagiyui.drive.component.captcha

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.service.CaptchaService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * 验证码切面
 * @author AkagiYui
 */
@Aspect
@Component
class CaptchaAspect(private val captchaService: CaptchaService) {

    @Around("@annotation(annotation)")
    fun aroundAdvice(joinPoint: ProceedingJoinPoint, annotation: CaptchaProtected): Any? {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val captchaId = request.getParameter(annotation.idParam)
        val captchaText: String? = request.getParameter(annotation.textParam)
        if (!captchaId.hasText() || !captchaText.hasText()
            || !captchaService.checkCaptcha(captchaId, captchaText)
        ) {
            throw CustomException(ResponseEnum.INVALID_CAPTCHA)
        }
        captchaService.deleteCaptcha(captchaId)
        return joinPoint.proceed()
    }

}
