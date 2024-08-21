package com.akagiyui.drive.component.permission

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.drive.entity.User
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
        // 检查是否登录
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication.principal == "anonymousUser") {
            throw CustomException(ResponseEnum.UNAUTHORIZED)
        }

        // 获取注解中的权限
        val permissions = requiredPermission.value
        val permissionList: Set<Permission> = permissions.toSet()
        if (permissionList.isEmpty()) {
            return // 无需权限
        }

        // 获取用户权限
        val user = authentication.principal as User
        val userPermissions = user.roles.filter { !it.disabled }.flatMap { it.permissions }.toSet()

        // 校验权限
        when (requiredPermission.mode) {
            RuleMode.AND -> {
                if (!userPermissions.containsAll(permissionList)) {
                    throw CustomException(ResponseEnum.UNAUTHORIZED)
                }
            }

            RuleMode.OR -> {
                if (userPermissions.intersect(permissionList).isEmpty()) {
                    throw CustomException(ResponseEnum.UNAUTHORIZED)
                }
            }
        }
    }

}
