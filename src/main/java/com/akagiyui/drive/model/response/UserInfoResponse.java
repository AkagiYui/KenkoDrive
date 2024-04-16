package com.akagiyui.drive.model.response;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.Permission;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 用户信息响应
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class UserInfoResponse {
    /**
     * 用户id
     */
    private String id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 已禁用
     */
    private boolean disabled;
    /**
     * 注册时间
     */
    private Date registerTime;

    /**
     * 权限
     */
    private List<String> permissions;

    public UserInfoResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.disabled = user.getDisabled();
        this.registerTime = user.getCreateTime();

        List<Role> roles = user.getRoles();
        List<Permission> allPermissions = roles.stream().flatMap(role -> role.getPermissions().stream()).toList();
        this.permissions = allPermissions.stream().map(Permission::name).toList();
    }

    public static List<UserInfoResponse> fromUserList(List<User> users) {
        return users.stream().map(UserInfoResponse::new).toList();
    }
}
