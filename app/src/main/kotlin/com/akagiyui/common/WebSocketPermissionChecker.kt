package com.akagiyui.common

import com.akagiyui.common.model.BeanWithPermissions
import com.akagiyui.drive.model.Permission
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

/**
 * WebSocket 权限检查器
 * @author AkagiYui
 */

class WebSocketPermissionChecker(val permissions: Set<Permission>) : HandshakeInterceptor {
    constructor(vararg permissions: Permission) : this(permissions.toSet())
    constructor(beanWithPermissions: BeanWithPermissions) : this(beanWithPermissions.permissions)

    private val permissionNames = permissions.map { it.name }

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        SecurityContextHolder.getContext().authentication?.authorities?.map { it.authority }?.let {
            if (it.containsAll(permissionNames)) {
                return true
            }
        }
        return false
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {
        // do nothing
    }

}
