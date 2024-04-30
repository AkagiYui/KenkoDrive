package com.akagiyui.drive.component.permission

import com.akagiyui.drive.model.Permission

/**
 * 权限校验注解
 *
 * @author AkagiYui
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequirePermission(
    vararg val value: Permission = [],
)
