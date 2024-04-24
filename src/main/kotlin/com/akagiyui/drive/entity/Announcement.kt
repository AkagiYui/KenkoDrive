package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert

/**
 * 公告实体
 *
 * @author AkagiYui
 */
@Entity
@Table
@DynamicInsert
class Announcement : BaseEntity() {
    /**
     * 标题
     */
    @Column(nullable = false)
    lateinit var title: String

    /**
     * 内容
     */
    var content: String? = null

    /**
     * 已启用
     */
    @Column(nullable = false)
    @ColumnDefault("true")
    var enabled: Boolean = true

    /**
     * 发布者
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    lateinit var author: User
}
