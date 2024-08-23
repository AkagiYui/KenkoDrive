package com.akagiyui.drive.service.impl

import cn.hutool.core.util.RandomUtil
import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.token.TokenTemplate
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.AddUserModel
import com.akagiyui.drive.model.CacheConstants
import com.akagiyui.drive.model.UserFilter
import com.akagiyui.drive.model.cache.EmailRegisterInfo
import com.akagiyui.drive.model.request.user.UpdateUserInfoRequest
import com.akagiyui.drive.repository.UserRepository
import com.akagiyui.drive.service.*
import jakarta.annotation.Resource
import jakarta.persistence.criteria.Predicate
import org.jetbrains.annotations.NotNull
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

/**
 * 用户服务实现类
 *
 * @author AkagiYui
 */
@Service
class UserServiceImpl(
    private val repository: UserRepository,
    private val redisCache: RedisCache,
    private val mailService: MailService,
    private val smsService: SmsService,
    private val settingService: SettingService,
    private val roleService: RoleService,
    private val ipRegionService: IpRegionService,
    private val tokenTemplate: TokenTemplate,
    private val passwordEncoder: PasswordEncoder,
) : UserService {
    private val log by LoggerDelegate()
    private var emailVerifyTimeout: Long = settingService.mailVerifyCodeTimeout.toLong()

    @Cacheable(value = [CacheConstants.USER_BY_ID], key = "#id")
    override fun getUserById(id: String): User {
        return repository.findById(id).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
    }

    override fun getUserByPhone(phone: String): User? {
        return repository.findByPhone(phone)
    }

    override fun getUserByUsernameOrEmailOrPhone(query: String): User? {
        return repository.getFirstByUsernameOrEmailOrPhone(query)
    }

    override fun getUsersByIds(@NotNull ids: List<String>): List<User> {
        return repository.findAllById(ids)
    }

    private fun findUserByIdWithCache(id: String): User {
        return repository.findById(id).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
    }

    @Cacheable(cacheNames = [CacheConstants.USER_PAGE], key = "{#index, #size, #userFilter }")
    override fun getUsers(index: Int, size: Int, userFilter: UserFilter?): Page<User> {
        val pageable: Pageable = PageRequest.of(index, size)

        val specification: Specification<User> = Specification { root, _, cb ->
            if (userFilter != null && userFilter.expression.hasText()) {
                val queryString = "%${userFilter.expression}%"
                val usernamePredicate: Predicate = cb.like(root["username"], queryString)
                val nicknamePredicate: Predicate = cb.like(root["nickname"], queryString)
                val emailPredicate: Predicate = cb.like(root["email"], queryString)
                cb.or(usernamePredicate, nicknamePredicate, emailPredicate)
            } else null
        }

        return repository.findAll(specification, pageable)
    }

    @Cacheable(cacheNames = [CacheConstants.USER_LIST])
    override fun getUsers(): List<User> {
        return repository.findAll()
    }

    @CacheEvict(
        cacheNames = [
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
            CacheConstants.USER_EXIST,
        ],
        allEntries = true,
    )
    override fun addUser(request: AddUserModel): User {
        val entity = User()
        entity.roles = roleService.getAllDefaultRoles() // 添加默认角色

        val currentTimestamp = System.currentTimeMillis()
        request.email.hasText {
            if (repository.existsByEmail(it)) {
                throw CustomException(ResponseEnum.EMAIL_EXIST)
            }
            entity.email = it
            entity.nickname = it
            entity.username = "email:$it:$currentTimestamp"
        }
        request.phone.hasText {
            if (repository.existsByPhone(it)) {
                throw CustomException(ResponseEnum.PHONE_EXIST)
            }
            entity.phone = it
            entity.nickname = it
            entity.username = "phone:$it:$currentTimestamp"
        }
        // 设置用户名，优先级最高，覆盖前面的设置
        request.username.hasText {
            if (repository.existsByUsername(it)) {
                throw CustomException(ResponseEnum.USER_EXIST)
            }
            entity.username = it
        }

        // 密码加密
        request.password.hasText { entity.password = encryptPassword(entity.username, it) }
        // 设置昵称
        request.nickname.hasText { entity.nickname = it }
        return repository.save(entity)
    }

    @CacheEvict(
        cacheNames = [
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_DETAILS,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
            CacheConstants.USER_EXIST,
        ],
        allEntries = true,
    )
    override fun deleteUser(id: String) {
        if (!repository.existsById(id)) {
            throw CustomException(ResponseEnum.NOT_FOUND)
        }
        repository.deleteById(id)
        // todo 删除用户文件关联
    }

    @Cacheable(cacheNames = [CacheConstants.USER_EXIST], key = "#id")
    override fun isUserExist(id: String): Boolean {
        return repository.existsById(id)
    }


    override fun registerByEmail(email: String, password: String) {
        if (!settingService.registerEnabled) {
            throw CustomException(ResponseEnum.REGISTER_DISABLED)
        }

        // 检查该邮箱是否在 redis 中等待验证
        val cacheKey = buildEmailRegisterCacheKey(email)
        if (cacheKey in redisCache) {
            throw CustomException(ResponseEnum.EMAIL_EXIST)
        }
        // 检查该邮箱是否已经注册
        if (repository.existsByEmail(email)) {
            throw CustomException(ResponseEnum.EMAIL_EXIST)
        }
        // 生成验证码
        val otp = RandomUtil.randomNumbers(6)
        mailService.sendEmailVerifyCode(email, otp, emailVerifyTimeout)
        // 将注册信息存入 redis
        val cacheModel = EmailRegisterInfo(email, password, otp)
        redisCache[cacheKey, emailVerifyTimeout + 1, TimeUnit.MINUTES] = cacheModel
    }

    private fun buildEmailRegisterCacheKey(email: String): String {
        return "emailRegisterInfo:$email"
    }

    /**
     * 用户服务，自我注入，以触发 AOP 代理
     */
    @Resource
    @Lazy
    lateinit var selfProxy: UserService

    override fun confirmRegister(email: String, otp: String) {
        // 从 redis 取回用户注册信息
        val cacheKey = buildEmailRegisterCacheKey(email)
        val cacheModel = redisCache.get<EmailRegisterInfo>(cacheKey)
            ?: throw CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND)
        // 检查验证码是否正确
        if (otp != cacheModel.otp) {
            throw CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND)
        }
        // 添加用户
        selfProxy.addUser(AddUserModel().apply {
            password = cacheModel.password
            this.email = cacheModel.email
        })
        // 删除 redis 中的注册信息
        redisCache.delete(cacheKey)
    }

    /**
     * 加密密码
     * @param username 用户名
     * @param password 密码明文
     * @return 密码密文
     */
    private fun encryptPassword(username: String, password: String): String {
        return encryptPassword(username, password, false)
    }

    /**
     * 加密密码
     * @param username 用户名
     * @param password 密码明文
     * @param raw 是否不通过加密器加密
     * @return 密码密文
     */
    private fun encryptPassword(username: String, password: String, raw: Boolean): String {
        val encode = password // 密码加密核心
        if (raw) {
            return encode
        }
        return passwordEncoder.encode(encode)
    }


    @Transactional
    override fun getPermission(user: User): Set<String> {
        return user.roles
            .asSequence()
            .map { it.permissions }
            .flatten()
            .map { it.name }
            .toSet()
    }

    override fun getRole(user: User): Set<String> {
        return user.roles.stream().map(Role::id).collect(Collectors.toSet())
    }

    @CacheEvict(
        cacheNames = [
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_DETAILS,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
            CacheConstants.USER_EXIST,
        ],
        allEntries = true,
    )
    override fun disable(id: String, disabled: Boolean) {
        val user = findUserByIdWithCache(id)
        // todo 检查是否为超级管理员
        user.disabled = disabled
        repository.save(user)
    }

    override fun resetPassword(id: String, newPassword: String) {
        val user = findUserByIdWithCache(id)
        user.password = encryptPassword(user.username, newPassword)
        repository.save(user)
    }

    @Transactional
    override fun addRoles(userId: String, id: Set<String>) {
        val user = findUserByIdWithCache(userId)
        val roles = roleService.find(id)
        user.roles.addAll(roles)
        repository.save(user)
    }

    @Transactional
    override fun removeRoles(userId: String, id: Set<String>) {
        val user = findUserByIdWithCache(userId)
        val roles = roleService.find(id)
        user.roles.removeAll(roles)
        repository.save(user)
    }

    @CacheEvict(
        cacheNames = [
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_DETAILS,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
        ],
        allEntries = true,
    )
    override fun updateInfo(id: String, userInfo: UpdateUserInfoRequest) {
        val user = findUserByIdWithCache(id)
        userInfo.nickname.hasText { user.nickname = it }
        userInfo.email.hasText { user.email = it }
        userInfo.phone.hasText { user.phone = it }
        userInfo.password.hasText { user.password = encryptPassword(user.username, it) }
        repository.save(user)
    }

    @Transactional
    override fun getRoles(id: String): Set<Role> {
        val user = findUserByIdWithCache(id)
        return HashSet(user.roles)
    }


}
