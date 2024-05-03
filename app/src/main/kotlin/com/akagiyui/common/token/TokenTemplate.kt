package com.akagiyui.common.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import java.util.*

/**
 * Token操作模板
 */
class TokenTemplate(
    secretKey: String,
    private val duration: Int,
) {
    private val jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    /**
     * 生成密钥
     *
     * @param userId 用户id
     * @return 密钥
     */
    fun createToken(userId: String): String {
        val currentTime = Calendar.getInstance()
        val expireTime = (currentTime.clone() as Calendar).apply {
            add(Calendar.HOUR, duration)
        }
        return Jwts.builder()
            .audience().add(userId).and()
            .issuedAt(currentTime.time)
            .notBefore(currentTime.time)
            .expiration(expireTime.time)
            .signWith(jwtKey)
            .compact()
    }

    /**
     * 获取密钥中的用户id
     *
     * @param token 密钥
     * @return 用户id
     */
    fun getUserId(token: String): String? {
        return try {
            val claims: Claims = Jwts.parser()
                .clockSkewSeconds(3 * 60) // 允许3分钟的时间差
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(token).payload
            claims.audience.firstOrNull()
        } catch (e: JwtException) {
            throw TokenVerifyException()
        }
    }
}
