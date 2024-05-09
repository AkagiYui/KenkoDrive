package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.Permission.PermissionConverter
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert

/**
 * 角色实体类
 *
 * @author AkagiYui
 */
@Entity
@Table
@DynamicInsert
class Role : BaseEntity() {
    /**
     * 角色名
     */
    @Column(nullable = false, unique = true)
    lateinit var name: String

    /**
     * 角色描述
     */
    var description: String? = null

    /**
     * 角色是否被禁用
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    var disabled: Boolean = false

    /**
     * 是否自动为新用户添加该角色
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    var isDefault: Boolean = false

    /**
     * 角色权限
     */
    @Convert(converter = PermissionConverter::class)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "role_permission")
    var permissions: MutableSet<Permission> = mutableSetOf()

    /**
     * 该角色下的用户
     */
    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    var users: MutableSet<User> = mutableSetOf()
}
