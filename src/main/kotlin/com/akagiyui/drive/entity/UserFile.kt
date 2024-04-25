package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert

/**
 * 文件与用户关联 实体类
 *
 * @author AkagiYui
 */
@Entity
@Table(
    name = "user_file",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "file_info_id", "folder_id"])]
)
@DynamicInsert
class UserFile : BaseEntity() {
    /**
     * 所属用户
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    lateinit var user: User

    /**
     * 文件信息
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_info_id", nullable = false)
    lateinit var fileInfo: FileInfo

    /**
     * 所属文件夹
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "folder_id")
    var folder: Folder? = null

    /**
     * 用户文件名（含后缀）
     */
    @Column(nullable = false)
    lateinit var name: String
}
