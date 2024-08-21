package com.akagiyui.drive.component.permission

/**
 * 权限校验注解权限组合模式
 * @author AkagiYui
 */
enum class RuleMode {
    AND, // 与，所有权限都需要满足
    OR // 或，只需要满足一个权限
}
