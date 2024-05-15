package com.akagiyui.drive.controller.websocket

import com.akagiyui.common.BroadcastWebSocketHandler
import com.akagiyui.common.collection.CircularBuffer
import com.akagiyui.common.model.WebSocketHandlerWithPermissions
import com.akagiyui.drive.model.Permission
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession


/**
 * 内存信息 WebSocket 处理器
 * @author AkagiYui
 */
@Component
class MemoryWebSocketHandler : BroadcastWebSocketHandler(), WebSocketHandlerWithPermissions {
    override val permissions = setOf(Permission.SYSTEM_INFO_GET)
    private val objectMapper = ObjectMapper()
    private val memoryInfoBuffer: CircularBuffer<Map<String, Number>> = CircularBuffer(60)

    @Scheduled(fixedDelay = 1000)
    private fun collectInfo() {
        val runtime = Runtime.getRuntime()
        val totalMemory = runtime.totalMemory()
        val freeMemory = runtime.freeMemory()

        val memoryInfo = mapOf(
            "time" to System.currentTimeMillis(), // 时间戳
            "totalMemory" to runtime.totalMemory(),
            "freeMemory" to runtime.freeMemory(),
            "usedMemory" to totalMemory - freeMemory,
            "maxMemory" to runtime.maxMemory()
        )
        memoryInfoBuffer.append(memoryInfo)
        broadcast(objectEncode(memoryInfo))
    }

    override fun afterConnectionEstablished(session: WebSocketSession) {
        // 发送历史数据
        session.sendMessage(objectEncode(memoryInfoBuffer.getAll()))
        super.afterConnectionEstablished(session)
    }

    private fun objectEncode(obj: Any): TextMessage {
        return TextMessage(objectMapper.writeValueAsString(obj))
    }
}
