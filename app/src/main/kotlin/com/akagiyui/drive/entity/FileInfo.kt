package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.DynamicInsert

/**
 * 文件信息实体
 *
 * @author AkagiYui
 */
@Entity
@Table
@DynamicInsert
class FileInfo : BaseEntity() {
    /**
     * 第一个上传的文件名
     */
    @Column(nullable = false)
    lateinit var name: String

    /**
     * 文件大小（字节Byte）
     */
    @Column(nullable = false)
    var size: Long = 0L

    /**
     * 文件类型
     */
    @Column(nullable = false)
    lateinit var type: String

    /**
     * 文件哈希
     */
    @Column(nullable = false, unique = true)
    lateinit var hash: String

    /**
     * 文件存储Key
     */
    @Column(nullable = false)
    lateinit var storageKey: String

    /**
     * 下载次数
     */
    @Column(nullable = false)
    @ColumnDefault("0")
    var downloadCount: Long = 0L

    /**
     * 锁定
     */
    @Column(nullable = false)
    @ColumnDefault("0")
    var locked: Boolean = false
}
