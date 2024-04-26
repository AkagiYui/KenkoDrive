package com.akagiyui.drive.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

/**
 * 设置项 实体类
 *
 * @author AkagiYui
 */
@Entity
@Table(name = "system_setting")
// 自动填充创建时间和更新时间
@EntityListeners(AuditingEntityListener::class)
class KeyValueSetting {
    /**
     * 设置项键
     *
     *
     * 注意不要使用"key"等关键字
     */
    @Id
    lateinit var settingKey: String

    /**
     * 设置项值
     *
     *
     * 注意不要使用"value"等关键字
     */
    @Column(nullable = false)
    lateinit var settingValue: String

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(nullable = false)
    lateinit var updateTime: Date

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    lateinit var createTime: Date
}
