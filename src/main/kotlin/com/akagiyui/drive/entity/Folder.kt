package com.akagiyui.drive.entity

import com.akagiyui.common.entity.BaseEntity
import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert

/**
 * 文件夹 实体
 *
 * @author AkagiYui
 */
@Entity
@Table(uniqueConstraints = [UniqueConstraint(columnNames = ["name", "parent_id", "user_id"])])
@DynamicInsert
class Folder : BaseEntity() {
    /**
     * 文件夹名
     */
    @Column(nullable = false)
    lateinit var name: String

    /**
     * 父文件夹
     */
    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.EAGER)
    var parent: Folder? = null

    /**
     * 所属用户
     */
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    lateinit var user: User

    /**
     * 是否在根目录下
     */
    val inRoot
        get() = parent == null

    /**
     * 获取完整路径
     */
    val path: String
        get() = if (inRoot) {
            "/$name"
        } else {
            "${parent!!.path}/$name"
        }

    /**
     * 获取层级路径列表
     */
    val pathList: MutableList<String?>
        get() = if (inRoot) {
            mutableListOf(name)
        } else {
            parent!!.pathList.apply {
                add(name)
            }
        }

}
