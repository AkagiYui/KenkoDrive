package com.akagiyui.drive.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 用户实体
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
@Entity
@Table
public class User extends BaseEntity {
    /**
     * 用户名
     */
    @Column(nullable = false)
    private String username;
    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 邮箱
     */
    @Column(nullable = false)
    private String email;
    /**
     * 已禁用
     */
    @Column(nullable = false)
    private Boolean disabled;
}
