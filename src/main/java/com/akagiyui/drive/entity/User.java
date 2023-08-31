package com.akagiyui.drive.entity;

import com.akagiyui.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;


/**
 * 用户实体
 *
 * @author AkagiYui
 */
@Data
@ToString(exclude = "roles")
@Accessors(chain = true)
@Entity
@Table
@DynamicInsert
public class User extends BaseEntity {
    /**
     * 用户名
     */
    @Column(nullable = false, unique = true)
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
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 已禁用
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean disabled;

    /**
     * 角色
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

}
