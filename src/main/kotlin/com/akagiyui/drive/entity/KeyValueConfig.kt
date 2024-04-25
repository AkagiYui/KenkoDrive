package com.akagiyui.drive.entity

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.Date

/**
 * 配置项 实体类
 *
 * @author AkagiYui
 */
@Entity
@Table(name = "system_config")
@EntityListeners(AuditingEntityListener::class)
class KeyValueConfig {
    /**
     * 配置项键
     *
     *
     * 注意不要使用"key"等关键字
     */
    @Id
    lateinit var configKey: String

    /**
     * 配置项值
     *
     *
     * 注意不要使用"varue"等关键字
     */
    @Column(nullable = false)
    lateinit var configValue: String

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
