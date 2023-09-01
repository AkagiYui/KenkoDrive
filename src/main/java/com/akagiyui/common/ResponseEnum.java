package com.akagiyui.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应状态枚举
 *
 * @author AkagiYui
 */
@Getter
@AllArgsConstructor
public enum ResponseEnum {
    /**
     * 成功
     */
    SUCCESS(10000, ""),
    /**
     * 错误
     */
    GENERAL_ERROR(10001, "General error"),
    /**
     * 内部错误
     */
    INTERNAL_ERROR(10002, "Internal error"),
    /**
     * 未找到
     */
    NOT_FOUND(10003, "Not found"),
    /**
     * 未认证
     */
    UNAUTHORIZED(10004, "Unauthorized"),
    /**
     * 用户已存在
     */
    USER_EXIST(10005, "User exist"),
    /**
     * 参数错误
     */
    BAD_REQUEST(10006, "Bad request"),
    /**
     * 未授权
     */
    FORBIDDEN(10007, "Forbidden"),
    /**
     * 邮箱已被使用
     */
    EMAIL_EXIST(10008, "Email exist"),
    /**
     * 验证码未找到
     */
    VERIFY_CODE_NOT_FOUND(10009, "Verify code not found"),
    /**
     * 请求过快
     */
    TOO_MANY_REQUESTS(10010, "Too many requests"),
    /**
     * 应用已存在
     */
    CLIENT_EXIST(10011, "Client exist"),
    /**
     * 应用不存在
     */
    CLIENT_NOT_FOUND(10012, "Client not found"),
    /**
     * 文件过大
     */
    FILE_TOO_LARGE(10013, "File too large"),
    /**
     * 文件格式不支持
     */
    FILE_FORMAT_NOT_SUPPORT(10014, "File format not support"),
    /**
     * 文件夹已存在
     */
    FOLDER_EXIST(10015, "Folder exist"),
    /**
     * 注册已关闭
     */
    REGISTER_DISABLED(10016, "Register disabled"),
    /**
     * 任务已存在
     */
    TASK_EXIST(10017, "Task exist"),
    ;

    /**
     * 状态码
     */
    private final Integer code;
    /**
     * 消息
     */
    private final String msg;
}
