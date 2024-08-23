package com.akagiyui.drive.service

import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.cache.TemporaryLoginInfo
import com.akagiyui.drive.model.response.auth.ClaimedTemporaryTokenInfoResponse

/**
 * 认证服务接口
 * @author AkagiYui
 */
interface AuthService {

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     */
    fun sendSmsOneTimePassword(phone: String)

    /**
     * 获取短信验证码
     *
     * @param phone 手机号
     * @param code 验证码
     */
    fun getAccessTokenBySms(phone: String, code: String): String

    /**
     * 获取访问令牌
     *
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌
     */
    fun getAccessToken(username: String, password: String): String

    /**
     * 获取访问令牌
     *
     * @param userId 用户ID
     * @return 访问令牌
     */
    fun getAccessToken(userId: String): String

    /**
     * 生成临时登录令牌
     *
     * @return 临时登录令牌
     */
    fun generateTemporaryLoginToken(): String

    /**
     * 获取临时登录令牌状态
     *
     * @param token 临时登录令牌
     * @return 临时登录令牌状态
     */
    fun getTemporaryLoginTokenStatus(token: String): TemporaryLoginInfo

    /**
     * 认领临时登录令牌
     *
     * @param token 临时登录令牌
     * @param user 用户
     * @param ip IP
     * @return 认领临时登录令牌信息
     */
    fun claimTemporaryLoginToken(token: String, user: User, ip: String): ClaimedTemporaryTokenInfoResponse

    /**
     * 确认临时登录令牌
     *
     * @param temporaryToken 临时登录令牌
     * @param takenToken 认领令牌
     * @param user 用户
     */
    fun confirmTemporaryLoginToken(temporaryToken: String, takenToken: String, user: User)

    /**
     * 取消临时登录令牌
     *
     * @param temporaryToken 临时登录令牌
     * @param takenToken 认领令牌
     * @param user 用户
     */
    fun cancelTemporaryLoginToken(temporaryToken: String, takenToken: String, user: User)

}
