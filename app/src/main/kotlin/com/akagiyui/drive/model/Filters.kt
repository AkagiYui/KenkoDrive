package com.akagiyui.drive.model

sealed class ModelFilter {
    /**
     * 表达式
     */
    var expression: String? = null

    /**
     * 用于序列化到缓存
     */
    override fun toString(): String {
        return expression ?: ""
    }
}

/**
 * 公告查询筛选
 * @author AkagiYui
 */
class AnnouncementFilter : ModelFilter()

/**
 * 角色查询筛选
 * @author AkagiYui
 */
class RoleFilter : ModelFilter()

/**
 * 用户查询筛选
 * @author AkagiYui
 */
class UserFilter : ModelFilter()

/**
 * 文件查询筛选
 */
class FileInfoFilter : ModelFilter()

/**
 * 文件夹内容查询筛选
 */
class FolderContentFilter : ModelFilter()
