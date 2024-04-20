package com.akagiyui.drive.config

import io.undertow.server.DefaultByteBufferPool
import io.undertow.websockets.jsr.WebSocketDeploymentInfo
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component

/**
 * Undertow WebSocket 连接池配置
 *
 * @author AkagiYui
 */
@Component
class UndertowPoolCustomizer : WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    override fun customize(factory: UndertowServletWebServerFactory) {
        factory.addDeploymentInfoCustomizers(
            UndertowDeploymentInfoCustomizer {
                val webSocketDeploymentInfo = WebSocketDeploymentInfo().apply {
                    // 设置缓存区
                    buffers = DefaultByteBufferPool(false, 1024)
                }
                it.addServletContextAttribute(
                    "io.undertow.websockets.jsr.WebSocketDeploymentInfo",
                    webSocketDeploymentInfo,
                )
            },
        )
    }
}
