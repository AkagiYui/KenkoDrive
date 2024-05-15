package com.akagiyui.common.model

import org.springframework.web.socket.WebSocketHandler

/**
 * 带权限的 WebSocket 处理器接口
 * @author AkagiYui
 */

interface WebSocketHandlerWithPermissions : BeanWithPermissions, WebSocketHandler
