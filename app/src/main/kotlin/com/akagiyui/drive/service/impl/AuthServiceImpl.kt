package com.akagiyui.drive.service.impl

import cn.hutool.core.util.RandomUtil.BASE_CHAR_NUMBER
import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.exception.UnAuthorizedException
import com.akagiyui.common.token.TokenTemplate
import com.akagiyui.common.utils.BASE_NUMBER
import com.akagiyui.common.utils.hasText
import com.akagiyui.common.utils.random
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.AddUserModel
import com.akagiyui.drive.model.CacheConstants
import com.akagiyui.drive.model.cache.TemporaryLoginInfo
import com.akagiyui.drive.model.response.auth.ClaimedTemporaryTokenInfoResponse
import com.akagiyui.drive.service.AuthService
import com.akagiyui.drive.service.IpRegionService
import com.akagiyui.drive.service.SmsService
import com.akagiyui.drive.service.UserService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

/**
 * 认证服务实现
 * @author AkagiYui
 */
@Service
class AuthServiceImpl(
    private val userService: UserService,
    private val tokenTemplate: TokenTemplate,
    private val smsService: SmsService,
    private val redisCache: RedisCache,
    private val ipRegionService: IpRegionService,
    private val passwordEncoder: PasswordEncoder,
) : AuthService {
    override fun getAccessToken(userId: String): String {
        return tokenTemplate.createToken(userId)
    }

    @Transactional
    override fun getAccessToken(username: String, password: String): String {
        val user = userService.getUserByUsernameOrEmailOrPhone(username) ?: throw UnAuthorizedException()
        if (!user.password.hasText()) {
            throw UnAuthorizedException()
        }
        if (!passwordEncoder.matches(password, user.password)) {
            throw UnAuthorizedException()
        }
        return getAccessToken(user.id)
    }

    override fun sendSmsOneTimePassword(phone: String) {
        val verifyCode = String.random(String.BASE_NUMBER, 6)
        val redisKey = "smsCode:$phone"
        redisCache[redisKey, 10, TimeUnit.MINUTES] = verifyCode
        // 发送短信验证码
        smsService.sendSmsOneTimePassword(phone, verifyCode)
    }

    @CacheEvict(
        cacheNames = [
            CacheConstants.USER_DETAILS,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
        ],
        allEntries = true,
    )
    override fun getAccessTokenBySms(phone: String, code: String): String {
        val redisKey = "smsCode:$phone"
        val verifyCode = redisCache.get<String>(redisKey) ?: throw CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND)
        if (verifyCode != code) {
            throw CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND)
        }
        redisCache.delete(redisKey)
        // 判断用户是否存在
        var user = userService.getUserByPhone(phone)
        if (user == null) {
            // 不存在则新增用户
            val newUser = AddUserModel().apply {
                this.phone = phone
            }
            user = userService.addUser(newUser)
        }

        return tokenTemplate.createToken(user.id)
    }

    override fun generateTemporaryLoginToken(): String {
        val token = String.random(BASE_CHAR_NUMBER, 16)
        val redisKey = "temporaryLoginToken:$token"
        redisCache[redisKey, 2, TimeUnit.MINUTES] = TemporaryLoginInfo(token)
        return token
    }

    override fun getTemporaryLoginTokenStatus(token: String): TemporaryLoginInfo {
        val redisKey = "temporaryLoginToken:$token"
        val info = redisCache.get<TemporaryLoginInfo>(redisKey) ?: throw CustomException(ResponseEnum.INVALID_TOKEN)
        if (info.confirmed || info.canceled) {
            // 确认或取消后，只允许查询一次
            redisCache.delete(redisKey)
        }
        return info
    }

    override fun claimTemporaryLoginToken(token: String, user: User, ip: String): ClaimedTemporaryTokenInfoResponse {
        val redisKey = "temporaryLoginToken:$token"
        val info = redisCache.get<TemporaryLoginInfo>(redisKey) ?: throw CustomException(ResponseEnum.INVALID_TOKEN)
        if (info.taken) { // todo 如果这里被两个人同时请求，会怎么样？
            throw CustomException(ResponseEnum.INVALID_TOKEN)
        }

        info.userId = user.id
        info.taken = true
        val takenToken = String.random(BASE_CHAR_NUMBER, 16)
        info.takenToken = takenToken
        redisCache[redisKey, 2, TimeUnit.MINUTES] = info // 延长有效期

        val ipRegion = ipRegionService.getRegion(ip)
        return ClaimedTemporaryTokenInfoResponse(takenToken, user.nickname, ipRegion)
    }

    override fun confirmTemporaryLoginToken(temporaryToken: String, takenToken: String, user: User) {
        val redisKey = "temporaryLoginToken:$temporaryToken"
        val info = redisCache.get<TemporaryLoginInfo>(redisKey) ?: throw CustomException(ResponseEnum.INVALID_TOKEN)
        if (!info.taken || info.userId != user.id || info.takenToken != takenToken || info.canceled) {
            throw CustomException(ResponseEnum.INVALID_TOKEN)
        }
        info.confirmed = true
        redisCache[redisKey, 2, TimeUnit.MINUTES] = info
    }

    override fun cancelTemporaryLoginToken(temporaryToken: String, takenToken: String, user: User) {
        val redisKey = "temporaryLoginToken:$temporaryToken"
        val info = redisCache.get<TemporaryLoginInfo>(redisKey) ?: throw CustomException(ResponseEnum.INVALID_TOKEN)
        if (!info.taken || info.userId != user.id || info.takenToken != takenToken || info.confirmed) {
            throw CustomException(ResponseEnum.INVALID_TOKEN)
        }
        info.canceled = true
        redisCache[redisKey, 2, TimeUnit.MINUTES] = info
    }


}
