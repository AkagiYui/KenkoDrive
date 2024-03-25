package com.akagiyui.drive.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.component.CacheConstants;
import com.akagiyui.drive.component.RedisCache;
import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.LoginUserDetails;
import com.akagiyui.drive.model.filter.UserFilter;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest;
import com.akagiyui.drive.model.request.RegisterConfirmRequest;
import com.akagiyui.drive.model.request.UpdateUserInfoRequest;
import com.akagiyui.drive.repository.UserRepository;
import com.akagiyui.drive.service.ConfigService;
import com.akagiyui.drive.service.MailService;
import com.akagiyui.drive.service.RoleService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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

    @Resource
    private ConfigService configService;

    @Resource
    private RoleService roleService;

    @Override
    @Cacheable(value = CacheConstants.USER_BY_ID, key = "#id")
    public User findUserById(String id) {
        return repository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public List<User> findUserByIds(List<String> ids) {
        return repository.findAllById(ids);
    }

    private User findUserByIdWithCache(String id) {
        return repository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public User register(User user) {
        return null;
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_PAGE, key = "{#index, #size, #filter}")
    public Page<User> find(int index, int size, UserFilter filter) {
        Pageable pageable = PageRequest.of(index, size);

        // 条件查询
        Specification<User> specification = (root, query, cb) -> {
            if (filter != null && StringUtils.hasText(filter.getExpression())){
                String queryString = "%" + filter.getExpression() + "%";
                Predicate usernamePredicate = cb.like(root.get("username"), queryString);
                Predicate nicknamePredicate = cb.like(root.get("nickname"), queryString);
                Predicate emailPredicate = cb.like(root.get("email"), queryString);
                return cb.or(usernamePredicate, nicknamePredicate, emailPredicate);
            }
            return null;
        };

        return repository.findAll(specification, pageable);
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_LIST)
    public List<User> find() {
        return repository.findAll();
    }

    @Override
    @CacheEvict(cacheNames = {
            CacheConstants.USER_BY_ID,
            CacheConstants.USER_PAGE,
            CacheConstants.USER_LIST,
            CacheConstants.USER_EXIST,
    }, allEntries = true)
    public String addUser(AddUserRequest user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new CustomException(ResponseEnum.USER_EXIST);
        }

        User realUser = user.toUser();
        if (StringUtils.hasText(user.getEmail())) {
            if (repository.existsByEmail(user.getEmail())) {
                throw new CustomException(ResponseEnum.EMAIL_EXIST);
            }
            realUser.setEmail(user.getEmail());
        }
        if (StringUtils.hasText(user.getNickname())) {
            realUser.setNickname(user.getNickname());
        }

        realUser.setPassword(encryptPassword(user.getUsername(), user.getPassword()));
        realUser.setDisabled(false);
        realUser.setRoles(roleService.getAllDefaultRoles());

        repository.save(realUser);
        return realUser.getId();
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
    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        repository.deleteById(id);
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
        LoginUserDetails userDetails = (LoginUserDetails)authentication.getPrincipal();
        return userDetails.getUser();
    }

    @Override
    @Cacheable(cacheNames = CacheConstants.USER_LOGIN_DETAILS, key = "#userId")
    @Transactional
    public LoginUserDetails getUserDetails(String userId) {
        User user = findUserById(userId);
        return new LoginUserDetails(user);
    }

    @Override
    public void sendEmailVerifyCode(EmailVerifyCodeRequest verifyRequest) {
        if (!configService.isRegisterEnabled()) {
            throw new CustomException(ResponseEnum.REGISTER_DISABLED);
        }

        // 检查该邮箱是否在 redis 中等待验证
        String redisKey = "emailVerifyCode:" + verifyRequest.getEmail();
        if (Objects.equals(Boolean.TRUE, redisCache.hasKey(redisKey))) {
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
    }

    @Override
    public void confirmRegister(RegisterConfirmRequest registerConfirmRequest) {
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
        } finally {
            // 删除 redis 中的验证码和注册信息
            redisCache.delete(redisKey);
            redisCache.delete("registerInfo:" + registerConfirmRequest.getEmail());
        }
    }

    @Override
    public String encryptPassword(String username, String password) {
        return encryptPassword(username, password, false);
    }

    @Override
    public String encryptPassword(String username, String password, boolean raw) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        // 密码加密核心
        String encode = password;
        if (raw) {
            return encode;
        }
        return passwordEncoder.encode(encode);
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
    public void updateInfo(UpdateUserInfoRequest userInfo) {
        User user = getUser();
        if (StringUtils.hasText(userInfo.getNickname())) {
            user.setNickname(userInfo.getNickname());
        }
        if (StringUtils.hasText(userInfo.getEmail())) {
            user.setEmail(userInfo.getEmail());
        }
        repository.save(user);
    }

    @Override
    @Transactional
    public Set<String> getPermission() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUserDetails userDetails = (LoginUserDetails)authentication.getPrincipal();
        return userDetails.getPermissions();
    }

    @Override
    public Set<String> getRole() {
        User user = getUser();
        return user.getRoles().stream().map(Role::getId).collect(Collectors.toSet());
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
    public void disable(String id, boolean disabled) {
        User user = findUserByIdWithCache(id);
        // todo 检查是否为超级管理员
        user.setDisabled(disabled);
        repository.save(user);
    }

    @Override
    public void resetPassword(String id, String newPassword) {
        User user = findUserByIdWithCache(id);
        user.setPassword(encryptPassword(user.getUsername(), newPassword));
        repository.save(user);
    }

    @Override
    @Transactional
    public void addRoles(String userId, Set<String> id) {
        User user = findUserByIdWithCache(userId);
        Set<Role> roles = roleService.find(id);
        user.getRoles().addAll(roles);
        repository.save(user);
    }

    @Override
    @Transactional
    public void removeRoles(String userId, Set<String> id) {
        User user = findUserByIdWithCache(userId);
        Set<Role> roles = roleService.find(id);
        user.getRoles().removeAll(roles);
        repository.save(user);
    }

    @Override
    public void updateInfo(String id, UpdateUserInfoRequest userInfo) {
        User user = findUserByIdWithCache(id);
        if (StringUtils.hasText(userInfo.getNickname())) {
            user.setNickname(userInfo.getNickname());
        }
        if (StringUtils.hasText(userInfo.getEmail())) {
            user.setEmail(userInfo.getEmail());
        }
        if (StringUtils.hasText(userInfo.getPassword())) {
            user.setPassword(encryptPassword(user.getUsername(), userInfo.getPassword()));
        }
        repository.save(user);
    }

    @Override
    @Transactional
    public Set<Role> getRoles(String id) {
        User user = findUserByIdWithCache(id);
        return new HashSet<>(user.getRoles());
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param loginUsernameParam 登录username参数
     */
    @Override
    @Cacheable(cacheNames = CacheConstants.USER_DETAILS, key = "#loginUsernameParam")
    @Transactional
    public UserDetails loadUserByUsername(String loginUsernameParam) throws UsernameNotFoundException {
        User user = repository.getFirstByUsernameOrEmail(loginUsernameParam);
        if (user == null) {
            throw new UsernameNotFoundException("Username or password error");
        }
        return new LoginUserDetails(user);
    }
}
