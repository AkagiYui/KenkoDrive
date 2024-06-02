package com.akagiyui.drive.model

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

/**
 * 权限枚举
 *
 * @author AkagiYui
 */
enum class Permission(val description: String) {
    /**
     * 个人文件上传
     */
    PERSONAL_UPLOAD("个人文件上传"),

    /**
     * 个人文件下载
     */
    PERSONAL_DOWNLOAD("个人文件下载"),

    /**
     * 创建文件夹
     */
    FOLDER_CREATE("创建文件夹"),

    /**
     * 删除文件夹
     */
    FOLDER_DELETE("删除文件夹"),

    /**
     * 获取角色信息
     */
    ROLE_VIEW("获取角色信息"),

    /**
     * 添加角色
     */
    ROLE_ADD("添加角色"),

    /**
     * 修改角色
     */
    ROLE_UPDATE("修改角色"),

    /**
     * 删除角色
     */
    ROLE_DELETE("删除角色"),

    /**
     * 分配角色
     */
    ROLE_ASSIGN("分配角色"),

    /**
     * 用户查看
     */
    USER_VIEW("用户查看"),

    /**
     * 用户添加
     */
    USER_ADD("用户添加"),

    /**
     * 用户修改
     */
    USER_UPDATE("用户修改"),

    /**
     * 用户删除
     */
    USER_DELETE("用户删除"),

    /**
     * 公告添加
     */
    ANNOUNCEMENT_ADD("公告添加"),

    /**
     * 公告修改
     */
    ANNOUNCEMENT_UPDATE("公告修改"),

    /**
     * 公告删除
     */
    ANNOUNCEMENT_DELETE("公告删除"),

    /**
     * 获取所有公告
     */
    ANNOUNCEMENT_GET_ALL("获取所有公告"),

    /**
     * 获取设置
     */
    CONFIGURATION_GET("获取设置"),

    /**
     * 修改设置
     */
    CONFIGURATION_UPDATE("修改设置"),

    /**
     * 前端启用调试模式
     */
    FRONTEND_ENABLE_DEBUG_MODE("前端启用调试模式"),

    /**
     * 获取操作日志
     */
    ACTION_LOG_GET("获取操作日志"),

    /**
     * 获取系统信息
     */
    SYSTEM_INFO_GET("获取系统信息"),

    /**
     * 获取所有文件列表
     */
    FILE_LIST_ALL("获取所有文件列表"),
    ;

    /**
     * 从数据库值映射到枚举常量
     */
    @Converter
    class PermissionConverter : AttributeConverter<Permission, String> {
        /**
         * 将枚举常量转换为数据库列值
         *
         * @param permission 枚举常量
         * @return 数据库列值
         */
        override fun convertToDatabaseColumn(permission: Permission): String {
            return permission.name
        }

        /**
         * 将数据库列值转换为枚举常量
         *
         * @param value 数据库列值
         * @return 枚举常量
         */
        override fun convertToEntityAttribute(value: String): Permission {
            return valueOf(value)
        }
    }
}
