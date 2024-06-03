package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert

/**
 * 用户实体
 *
 * @author AkagiYui
 */
@Entity
@Table(name = "user_info") // user/system_user均会造成h2数据库关键字冲突
@DynamicInsert
class User : BaseEntity() {
    /**
     * 用户名，当前仅作为加密盐
     */
    @Column(unique = true, nullable = false)
    lateinit var username: String

    /**
     * 密码
     */
    @Column
    var password: String? = null

    /**
     * 昵称
     */
    var nickname: String? = null

    /**
     * 邮箱
     */
    @Column(unique = true)
    var email: String? = null

    /**
     * 手机号
     */
    @Column(unique = true)
    var phone: String? = null

    /**
     * 已禁用
     */
    @Column(nullable = false)
    @ColumnDefault("false")
    var disabled: Boolean = false

    /**
     * 角色
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
}
