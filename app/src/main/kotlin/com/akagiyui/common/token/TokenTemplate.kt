package com.akagiyui.common.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.DecodingException
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

/**
 * Token操作模板
 */
class TokenTemplate(
    secretKey: String,
    private val duration: Int,
) {
    private val jwtKey: SecretKey = try {
        val bytes = Decoders.BASE64.decode(secretKey)
        Keys.hmacShaKeyFor(bytes)
    } catch (e: DecodingException) {
        throw Base64DecodeException(e.message)
    }

    /**
     * 生成密钥
     *
     * @param userId 用户ID
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
     * 获取密钥中的用户ID
     *
     * @param token 密钥
     * @return 用户ID
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
