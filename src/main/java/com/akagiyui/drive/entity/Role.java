package com.akagiyui.drive.entity;

import com.akagiyui.common.entity.BaseEntity;
import com.akagiyui.drive.model.Permission;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import java.util.Set;

/**
 * 角色实体类
 *
 * @author AkagiYui
 */
@Data
@ToString(exclude = "users")
@Accessors(chain = true)
@Entity
@Table
@DynamicInsert
public class Role extends BaseEntity {
    /**
     * 角色名
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 角色是否被禁用
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean disabled;

    /**
     * 是否自动为新用户添加该角色
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    private Boolean isDefault;

    /**
     * 角色权限
     */
    @Convert(converter = Permission.PermissionConverter.class)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permission")
    private Set<Permission> permissions;

    /**
     * 该角色下的用户
     */
    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
