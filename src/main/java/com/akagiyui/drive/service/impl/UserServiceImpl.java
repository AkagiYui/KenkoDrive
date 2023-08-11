package com.akagiyui.drive.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.akagiyui.drive.component.CacheConstants;
import com.akagiyui.drive.component.RedisCache;
import com.akagiyui.drive.component.ResponseEnum;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.exception.CustomException;
import com.akagiyui.drive.model.LoginUserDetails;
import com.akagiyui.drive.model.filter.UserFilter;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest;
import com.akagiyui.drive.model.request.RegisterConfirmRequest;
import com.akagiyui.drive.model.request.UpdateUserInfoRequest;
import com.akagiyui.drive.repository.UserRepository;
import com.akagiyui.drive.service.MailService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 用户服务实现类
 *
 * @author AkagiYui
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    UserRepository repository;

    @Resource
    RedisCache redisCache;

    @Value("${application.email.verify.timeout}")
    private long emailVerifyTimeout;

    @Resource
    MailService mailService;

    @Resource
    @Lazy
    PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(value = CacheConstants.USER_BY_ID, key = "#id")
    public User findUserById(String id) {
        return repository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public User register(User user) {
        return null;
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_PAGE, key = "{#index, #size, #userFilter}")
    public Page<User> find(int index, int size, UserFilter userFilter) {
        Pageable pageable = PageRequest.of(index, size);
        return repository.findAll(pageable);
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_LIST)
    public List<User> find() {
        return repository.findAll();
    }

    @Override
    public boolean addUser(AddUserRequest user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new CustomException(ResponseEnum.USER_EXIST);
        }
        if (repository.existsByEmail(user.getEmail())) {
            throw new CustomException(ResponseEnum.EMAIL_EXIST);
        }

        if (!StringUtils.hasText(user.getNickname())) {
            user.setNickname(user.getUsername());
        }
        User realUser = user.toUser();
        realUser.setPassword(encryptPassword(user.getUsername(), user.getPassword()));
        realUser.setDisabled(false);

        repository.save(realUser);
        return true;
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_DETAILS,
            CacheConstants.USER_LOGIN_DETAILS,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
            CacheConstants.USER_EXIST,
    }, allEntries = true)
    public boolean delete(String id) {
        if (!repository.existsById(id)) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        repository.deleteById(id);
        return true;
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_EXIST, key = "#username")
    public boolean isExist(String username) {
        return repository.existsById(username);
    }

    @Override
    public User getUser() {
        // 从 SecurityContextHolder 中获取用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUserDetails userDetails = (LoginUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_LOGIN_DETAILS, key = "#userId")
    public LoginUserDetails getUserDetails(String userId) {
        User user = findUserById(userId);
        return new LoginUserDetails(user, List.of("ROLE_USER"));
    }

    @Override
    public boolean sendEmailVerifyCode(EmailVerifyCodeRequest verifyRequest) {
        // 检查该邮箱是否在 redis 中等待验证
        String redisKey = "emailVerifyCode:" + verifyRequest.getEmail();
        if (redisCache.hasKey(redisKey)) {
            throw new CustomException(ResponseEnum.EMAIL_EXIST);
        }
        // 检查该邮箱是否已经注册
        if (repository.existsByEmail(verifyRequest.getEmail())) {
            throw new CustomException(ResponseEnum.EMAIL_EXIST);
        }
        // 检查用户名是否已经注册
        if (repository.existsByUsername(verifyRequest.getUsername())) {
            throw new CustomException(ResponseEnum.EMAIL_EXIST);
        }
        // 生成验证码
        String verifyCode = RandomUtil.randomNumbers(6);
        redisCache.set(redisKey, verifyCode);
        redisCache.expire(redisKey, emailVerifyTimeout, TimeUnit.MINUTES);
        mailService.sendEmailVerifyCode(verifyRequest.getEmail(), verifyCode, emailVerifyTimeout);
        // 将注册信息存入 redis
        String registerInfoKey = "registerInfo:" + verifyRequest.getEmail();
        redisCache.set(registerInfoKey, verifyRequest);
        redisCache.expire(registerInfoKey, emailVerifyTimeout + 1, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public boolean confirmRegister(RegisterConfirmRequest registerConfirmRequest) {
        // 从 redis 取回验证码
        String redisKey = "emailVerifyCode:" + registerConfirmRequest.getEmail();
        String verifyCode = redisCache.get(redisKey);
        if (verifyCode == null) {
            throw new CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND);
        }
        // 检查验证码是否正确
        if (!Objects.equals(registerConfirmRequest.getVerifyCode(), verifyCode)) {
            throw new CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND);
        }
        // 从 redis 取回用户注册信息
        EmailVerifyCodeRequest verifyRequest = redisCache.get("registerInfo:" + registerConfirmRequest.getEmail());
        if (verifyRequest == null) {
            log.error("Register info not found: {}", registerConfirmRequest.getEmail());
            throw new CustomException(ResponseEnum.VERIFY_CODE_NOT_FOUND);
        }
        // 转换为用户对象
        User user = new User();
        user.setUsername(verifyRequest.getUsername());
        user.setPassword(encryptPassword(verifyRequest.getUsername(), verifyRequest.getPassword()));
        user.setEmail(verifyRequest.getEmail());
        user.setDisabled(false);
        try {
            repository.save(user);
            return true;
        } finally {
            // 删除 redis 中的验证码和注册信息
            redisCache.delete(redisKey);
            redisCache.delete("registerInfo:" + registerConfirmRequest.getEmail());
        }
    }

    @Override
    public String encryptPassword(String username, String password) {
        return passwordEncoder.encode(username + password);
    }

    @Override
    public String encryptPassword(String username, String password, boolean raw) {
        if (raw) {
            return username + password;
        }
        return encryptPassword(username, password);
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_DETAILS,
            CacheConstants.USER_LOGIN_DETAILS,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
            CacheConstants.USER_EXIST,
    }, allEntries = true)
    public boolean updateInfo(UpdateUserInfoRequest userInfo) {
        User user = getUser();
        if (StringUtils.hasText(userInfo.getNickname())) {
            user.setNickname(userInfo.getNickname());
        }
        if (StringUtils.hasText(userInfo.getEmail())) {
            user.setEmail(userInfo.getEmail());
        }
        repository.save(user);
        return true;
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     */
    @Override
    @Cacheable(cacheNames = CacheConstants.USER_DETAILS, key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.getFirstByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username or password error");
        }
        return new LoginUserDetails(user, List.of("ROLE_USER"));
    }
}
