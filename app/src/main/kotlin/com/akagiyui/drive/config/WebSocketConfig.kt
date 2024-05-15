package com.akagiyui.drive.config

import com.akagiyui.common.WebSocketPermissionChecker
import com.akagiyui.common.model.WebSocketHandlerWithPermissions
import com.akagiyui.drive.controller.websocket.MemoryWebSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


/**
 * WebSocket 配置
 * @author AkagiYui
 */
@Configuration
@EnableWebSocket // 开启 WebSocket
class WebSocketConfig(
    private val memoryWebSocketHandler: MemoryWebSocketHandler,
) : WebSocketConfigurer {
    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        // 注册 WebSocket 处理器
        addHandler(registry, memoryWebSocketHandler, "/system/memory")
    }

    private fun addHandler(registry: WebSocketHandlerRegistry, handler: WebSocketHandlerWithPermissions, path: String) {
        registry.addHandler(handler, path)
            .setAllowedOrigins("*") // 允许跨域
            .addInterceptors(WebSocketPermissionChecker(handler)) // 添加权限检查
    }
}

