package com.akagiyui.common.entity

import com.akagiyui.common.utils.SnowFlakeIdGenerator
import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.util.*

/**
 * 基础实体类
 * @author AkagiYui
 */
// 启用自动填充
@EntityListeners(AuditingEntityListener::class)
// 实体继承映射，标记该类为父类，为子类提供映射规则
@MappedSuperclass
abstract class BaseEntity : Serializable {

    /**
     * 记录ID
     */
    // 主键生成策略
    @Id
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(name = "snowflakeId", type = SnowFlakeIdGenerator::class)
    lateinit var id: String

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(nullable = false)
    var updateTime: Date? = null

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createTime: Date? = null
}
