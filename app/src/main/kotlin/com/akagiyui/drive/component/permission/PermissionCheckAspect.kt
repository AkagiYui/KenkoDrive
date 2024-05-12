package com.akagiyui.drive.component.permission

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.drive.model.Permission
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

/**
 * 权限校验切面
 *
 * @author AkagiYui
 */
@Aspect
@Component
class PermissionCheckAspect {

    @Before("@annotation(requiredPermission)")
    @PreAuthorize("isAuthenticated()")
    fun checkPermission(requiredPermission: RequirePermission) {
        // 获取当前用户的权限
        val authentication = SecurityContextHolder.getContext().authentication
        val authorities: Set<Permission> = authentication.authorities.map { Permission.valueOf(it.authority) }.toSet()

        // 获取注解中的权限
        val permissions = requiredPermission.value
        val permissionList: Set<Permission> = permissions.toSet()

        // 校验权限
        if (!authorities.containsAll(permissionList)) {
            throw CustomException(ResponseEnum.UNAUTHORIZED)
        }
    }

}
