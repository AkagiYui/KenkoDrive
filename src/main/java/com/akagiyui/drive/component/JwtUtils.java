package com.akagiyui.drive.component;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.jwt.JWT;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.LoginUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * JWT工具类
 * @author AkagiYui
 */
@Component
public class JwtUtils {
    /**
     * 密钥
     */
    @Value("${application.jwt.key:DEFAULT_KEY}")
    private byte[] key;
    /**
     * 过期时间
     */
    @Value("${application.jwt.timeout}")
    private int expireTime;

    /**
     * 生成密钥
     * @param user 用户
     * @return 密钥
     */
    public String createJwt(User user) {
        return createJwt(user.getId());
    }

    /**
     * 生成密钥
     * @param user 用户
     * @return 密钥
     */
    public String createJwt(LoginUserDetails user) {
        return createJwt(user.getUser());
    }

    /**
     * 生成密钥
     * @param userId 用户id
     * @return 密钥
     */
    public String createJwt(String userId) {
        DateTime currentTime = DateTime.now();
        return JWT.create()
                .setPayload("id", userId)
                .setKey(key)
                .setIssuedAt(currentTime)
                .setNotBefore(currentTime)
                .setExpiresAt(DateTime.now().offset(DateField.HOUR, expireTime))
                .sign();
    }

    /**
     * 验证密钥
     *
     * @param token 密钥
     * @return 是否有效
     */
    public boolean verifyJwt(String token) {
        return JWT.of(token).setKey(key).validate(1);
    }

    /**
     * 获取密钥中的用户id
     *
     * @param token 密钥
     * @return 用户id
     */
    public String getUserId(String token) {
        return  JWT.of(token).setKey(key).getPayloads().getStr("id");
    }
}
