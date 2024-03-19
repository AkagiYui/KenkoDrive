package com.akagiyui.drive.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限枚举
 *
 * @author AkagiYui
 */
@Getter
@AllArgsConstructor
@Slf4j
public enum Permission {
    /**
     * 个人文件上传
     */
    PERSONAL_UPLOAD("个人文件上传"),
    /**
     * 个人文件下载
     */
    PERSONAL_DOWNLOAD("个人文件下载"),

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
    ;

    /**
     * 权限描述
     */
    private final String description;

    /**
     * 从数据库值映射到枚举常量
     */
    @Converter
    public static class PermissionConverter implements AttributeConverter<Permission, String> {
        /**
         * 将枚举常量转换为数据库列值
         *
         * @param permission 枚举常量
         * @return 数据库列值
         */
        @Override
        public String convertToDatabaseColumn(Permission permission) {
            return permission.name();
        }

        /**
         * 将数据库列值转换为枚举常量
         *
         * @param value 数据库列值
         * @return 枚举常量
         */
        @Override
        public Permission convertToEntityAttribute(String value) {
            return Permission.valueOf(value);
        }
    }
}
