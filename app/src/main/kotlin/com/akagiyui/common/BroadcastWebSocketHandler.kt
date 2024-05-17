package com.akagiyui.common

import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import java.util.concurrent.CopyOnWriteArrayList


/**
 * 可广播消息的 WebSocket 处理器
 * @author AkagiYui
 */

abstract class BroadcastWebSocketHandler : AbstractWebSocketHandler() {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session) // 新的WebSocket连接打开时，添加session到列表
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session) // WebSocket连接关闭时，从列表中移除session
    }

    fun broadcast(message: WebSocketMessage<*>) {
        // 广播消息给所有连接的客户端
        for (webSocketSession in sessions) {
            if (webSocketSession.isOpen) {
                webSocketSession.sendMessage(message)
            }
        }
    }

    /**
     * 关闭所有连接
     */
    fun closeAll() {
        val sessions = this.sessions.toList()
        for (webSocketSession in sessions) {
            webSocketSession.close()
        }
    }
}
