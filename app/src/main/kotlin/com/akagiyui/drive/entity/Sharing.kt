package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert

/**
 * 分享实体
 * @author AkagiYui
 */
@Entity
@Table
@DynamicInsert
class Sharing : BaseEntity() {

    /**
     * 分享者
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    /**
     * 密码
     */
    @Column
    var password: String? = null

    /**
     * 文件
     */
    @OneToOne(fetch = FetchType.EAGER)
    lateinit var file: UserFile
}
