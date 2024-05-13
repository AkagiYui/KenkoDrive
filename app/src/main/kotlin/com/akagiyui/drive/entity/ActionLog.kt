package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.DynamicInsert

/**
 * 分享实体
 * @author AkagiYui
 */
@Entity
@Table
@DynamicInsert
class ActionLog : BaseEntity() {

    /**
     * 操作者，一般为用户名，SYSTEM表示系统操作
     */
    @Column(nullable = false)
    var operator: String = "SYSTEM"

    /**
     * 操作类型
     */
    @Column(nullable = false)
    lateinit var type: String

    /**
     * 操作对象
     */
    @Column(nullable = false)
    lateinit var action: String
}
