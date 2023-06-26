package com.akagiyui.drive.entity;

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
    private String username;
    /**
     * 密码
     */
    private String password;
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
    private Boolean disabled;
}
