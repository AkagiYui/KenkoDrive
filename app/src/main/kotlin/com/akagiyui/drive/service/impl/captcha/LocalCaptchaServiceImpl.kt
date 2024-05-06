package com.akagiyui.drive.service.impl.captcha

import cn.hutool.core.util.IdUtil
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.service.CaptchaService
import com.wf.captcha.SpecCaptcha
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

/**
 * 本地验证码服务实现类
 */
@Service
class LocalCaptchaServiceImpl(
    private val redisCache: RedisCache,
) : CaptchaService {
    private val idGenerator = IdUtil.getSnowflake(1)

    override fun createCaptcha(): Pair<String, String> {
        val captcha = SpecCaptcha()
        val id = idGenerator.nextIdStr()
        redisCache["captcha:$id", 10, TimeUnit.MINUTES] = captcha.text().lowercase()
        return Pair(id, captcha.toBase64())
    }

    override fun checkCaptcha(captchaId: String, text: String): Boolean {
        if (!text.hasText()) {
            return false
        }
        val key = "captcha:$captchaId"
        val captchaText: String = redisCache[key] ?: return false
        return captchaText == text.trim().lowercase()
    }

    override fun deleteCaptcha(captchaId: String) {
        val key = "captcha:$captchaId"
        redisCache.delete(key)
    }
}
