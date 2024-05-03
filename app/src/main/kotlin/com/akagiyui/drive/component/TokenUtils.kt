package com.akagiyui.drive.component

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

/**
 * Token工具类
 */
@Component
class TokenUtils {

    private val log by LoggerDelegate()

    /**
     * 密钥
     */
    @Value("\${application.token.key}")
    private val secretKey: String = "DEFAULT_KEY"

    /**
     * 过期时间
     */
    @Value("\${application.token.timeout}")
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
     * @param userId 用户id
     * @return 密钥
     */
    fun createToken(userId: String): String {
        val currentTime = Calendar.getInstance()
        val expireTime = (currentTime.clone() as Calendar).apply {
            add(Calendar.HOUR, expireTime)
        }
        return Jwts.builder()
            .audience().add(userId).and()
            .issuedAt(currentTime.time)
            .notBefore(currentTime.time)
            .expiration(expireTime.time)
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
            .compact()
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
            Jwts.parser()
                .clockSkewSeconds(3 * 60) // 允许3分钟的时间差
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parse(token)
            true
        } catch (e: JwtException) {
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
            val claims: Claims = Jwts.parser()
                .clockSkewSeconds(3 * 60) // 允许3分钟的时间差
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)))
                .build()
                .parseSignedClaims(token).payload
            claims.audience.firstOrNull()
        } catch (e: Exception) {
            log.error("获取用户ID错误", e)
            null
        }
}
