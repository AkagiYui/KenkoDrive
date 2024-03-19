package com.akagiyui.drive.model;

import com.akagiyui.drive.entity.Role;
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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 登录用户详情
 * @author AkagiYui
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDetails implements UserDetails, Serializable {

    /**
     * 用户
     */
    User user;

    /**
     * 权限字符串
     */
    private Set<String> permissions;

    /**
     * 权限列表
     */
    @JsonIgnore
    private Set<GrantedAuthority> authorities;

    /**
     * 登录用户详情
     * @param user 用户
     */
    public LoginUserDetails(User user) {
        this.user = user;

        // 获取权限，注意如果使用懒加载，需要在事务内获取
        List<Role> roles = user.getRoles();
        this.permissions = roles.stream()
                .map(Role::getPermissions)
                .flatMap(Collection::stream)
                .map(Permission::name)
                .collect(Collectors.toSet());
    }

    /**
     * 获取用户权限
     * @return 用户权限集合
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 如果 authorities 为空，则初始化，缓存加速
        return Objects.requireNonNullElseGet(authorities, () -> {
            // 转换为 GrantedAuthority 集合
            authorities = permissions.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
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
