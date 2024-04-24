package com.akagiyui.drive.component

import cn.hutool.core.date.DateField
import cn.hutool.core.date.DateTime
import cn.hutool.jwt.JWT
import cn.hutool.jwt.JWTException
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.LoginUserDetails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * Token工具类
 */
@Component
class TokenUtils {

    private val log by LoggerDelegate()

    /**
     * 密钥
     */
    @Value("\${application.jwt.key}")
    private val secretKey: ByteArray = "DEFAULT_KEY".toByteArray()

    /**
     * 过期时间
     */
    @Value("\${application.jwt.timeout}")
    private val expireTime: Int = 0

    /**
     * 生成密钥
     *
     * @param user 用户
     * @return 密钥
     */
    fun createToken(user: User): String {
        return createToken(user.id)
    }

    /**
     * 生成密钥
     *
     * @param user 用户
     * @return 密钥
     */
    fun createToken(user: LoginUserDetails): String {
        return createToken(user.user)
    }

    /**
     * 生成密钥
     *
     * @param userId 用户id
     * @return 密钥
     */
    fun createToken(userId: String): String {
        val currentTime = DateTime.now().apply {
            setMutable(false)
        }
        return JWT.create()
            .setPayload("id", userId)
            .setKey(secretKey)
            .setIssuedAt(currentTime)
            .setNotBefore(currentTime)
            .setExpiresAt(currentTime.offset(DateField.HOUR, expireTime))
            .sign()
    }

    /**
     * 验证密钥
     *
     * @param token 密钥
     * @return 是否有效
     */
    fun verifyToken(token: String): Boolean {
        if (token.length <= 20) {
            return false
        }
        return try {
            JWT.of(token).setKey(secretKey).validate(1)
        } catch (e: JWTException) {
            false
        } catch (e: Exception) {
            log.error("JWT验证错误", e)
            false
        }
    }

    /**
     * 获取密钥中的用户id
     *
     * @param token 密钥
     * @return 用户id
     */
    fun getUserId(token: String): String? =
        try {
            JWT.of(token).setKey(secretKey).payloads.getStr("id")
        } catch (e: Exception) {
            log.error("获取用户ID错误", e)
            null
        }
}
