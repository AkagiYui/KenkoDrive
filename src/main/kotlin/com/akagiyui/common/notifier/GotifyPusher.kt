package com.akagiyui.common.notifier

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

/**
 * Gotify 推送器
 * @author AkagiYui
 */
class GotifyPusher(
    /**
     * Gotify 服务器地址
     * 例如：http://localhost:8080
     */
    private val host: String,
    /**
     * Gotify Application API Key
     */
    apiKey: String,
) {
    /**
     * Gotify 请求头
     */
    private val headers: HttpHeaders

    /**
     * HTTP 请求模板
     */
    private val restTemplate: RestTemplate

    init {
        if (!host.startsWith("http")) {
            throw IllegalArgumentException("host must start with http or https")
        }
        while (host.endsWith("/")) {
            host + host.substring(0, host.length - 1)
        }

        headers = HttpHeaders()
        headers.add("X-Gotify-Key", apiKey)
        headers.contentType = MediaType.APPLICATION_JSON

        restTemplate = RestTemplate()
    }

    /**
     * 推送消息
     * @param title 消息标题
     * @param message 消息内容
     * @param priority 消息优先级
     * @return HTTP 响应
     */
    fun push(
        title: String,
        message: String,
        priority: Int = 5,
    ): ResponseEntity<Void> {

        val messageMap = mapOf(
            "message" to message,
            "title" to title,
            "priority" to priority,
        )

        val entity = HttpEntity(
            ObjectMapper().writeValueAsString(messageMap),
            headers,
        )

        return restTemplate.exchange(
            "$host/message",
            HttpMethod.POST,
            entity,
            Void::class.java,
        )
    }

}
