package com.akagiyui.drive.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * 用户实体
 * @author AkagiYui
 */
@Data
@Entity
@Table
public class User extends BaseEntity {
    /**
     * 用户名
     */
    String username;
    /**
     * 密码
     */
    String password;
    /**
     * 昵称
     */
    String nickname;
    /**
     * 邮箱
     */
    String email;
}
