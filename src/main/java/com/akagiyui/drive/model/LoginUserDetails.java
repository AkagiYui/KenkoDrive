package com.akagiyui.drive.model;

import com.akagiyui.drive.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 登录用户详情
 * @author AkagiYui
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDetails implements UserDetails, Serializable {
    User user;
    List<String> permissions;
    @JsonIgnore
    List<GrantedAuthority> authorities;

    /**
     * 登录用户详情
     * @param user 用户
     * @param permissions 用户权限
     */
    public LoginUserDetails(User user, List<String> permissions) {
        this.user = user;
        this.permissions = permissions;
    }

    /**
     * 获取用户权限
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 转换为 GrantedAuthority 集合
        return Objects.requireNonNullElseGet(authorities, () -> {
            authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return authorities;
        });
    }

    /**
     * 获取用户密码
     * @return 用户密码
     */
    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getUsername();
    }

    /**
     * 用户账号是否未过期
     * @return 用户账号是否未过期
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 用户账号是否未被锁定
     * @return 用户账号是否未被锁定
     */
    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 用户密码是否未过期
     * @return 用户密码是否未过期
     */
    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 用户是否可用
     * @return 用户是否可用
     */
    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return !user.getDisabled();
    }
}
