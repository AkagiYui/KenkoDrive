package com.akagiyui.drive.model

sealed class ModelFilter {
    /**
     * 表达式
     */
    var expression: String? = null
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
