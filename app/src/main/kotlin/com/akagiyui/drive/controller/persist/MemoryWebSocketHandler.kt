package com.akagiyui.drive.controller.persist

import com.akagiyui.common.BroadcastWebSocketHandler
import com.akagiyui.common.model.WebSocketHandlerWithPermissions
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.service.SystemService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession


/**
 * 内存信息 WebSocket 处理器
 * @author AkagiYui
 */
@Component
class MemoryWebSocketHandler(private val systemService: SystemService) : BroadcastWebSocketHandler(),
    WebSocketHandlerWithPermissions, InitializingBean, DisposableBean {
    override val permissions = setOf(Permission.SYSTEM_INFO_GET)
    private val objectMapper = ObjectMapper()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // 发送历史数据
        session.sendMessage(objectEncode(systemService.getMemoryInfoHistory()))
        super.afterConnectionEstablished(session)
    }

    private fun objectEncode(obj: Any): TextMessage {
        return TextMessage(objectMapper.writeValueAsString(obj))
    }

    private fun callback(memoryInfo: Map<String, Any>) {
        broadcast(objectEncode(memoryInfo))
    }

    override fun afterPropertiesSet() {
        systemService.addRealTimeInfoCallback(::callback)
    }

    override fun destroy() {
        systemService.removeRealTimeInfoCallback(::callback)
        closeAll()
    }
}
