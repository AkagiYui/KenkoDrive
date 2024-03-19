package com.akagiyui.drive.component.permission;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.model.Permission;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限校验切面
 * @author AkagiYui
 */
@Aspect
@Component
public class PermissionAspect {

    @Before("@annotation(requiredPermission)")
    @PreAuthorize("isAuthenticated()")
    public void checkPermission(RequirePermission requiredPermission) {
        // 获取当前用户的权限
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Set<String> authorities = userDetails.getAuthorities().stream().map(Object::toString).collect(Collectors.toSet());

        // 获取注解中的权限
        Permission[] permissions = requiredPermission.value();
        Set<String> permissionList = Arrays.stream(permissions).map(Permission::name).collect(Collectors.toSet());

        // 校验权限
        if (!authorities.containsAll(permissionList)) {
            throw new CustomException(ResponseEnum.UNAUTHORIZED);
        }
    }
}
