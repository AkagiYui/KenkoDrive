package com.akagiyui.common.notifier

import com.akagiyui.common.delegate.LoggerDelegate
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import java.net.URI

/**
 * Gotify 推送器
 * @author AkagiYui
 */
class GotifyPusher(
    /**
     * Gotify 服务器地址
     * 例如：http://localhost:8080
     */
    private var host: String,
    /**
     * Gotify Application API Key
     */
    apiKey: String,
) {
    private val log by LoggerDelegate()

    /**
     * Gotify 请求头
     */
    private val headers: HttpHeaders

    /**
     * HTTP 请求模板
     */
    private val restTemplate: RestTemplate

    /**
     * JSON 对象映射器
     */
    private val objectMapper: ObjectMapper

    init {
        require(host.startsWith("http")) {
            "host must start with http or https"
        }
        // 去除末尾的斜杠
        host = URI.create(host).normalize().toString()

        headers = HttpHeaders()
        headers.add("X-Gotify-Key", apiKey)
        headers.contentType = MediaType.APPLICATION_JSON

        restTemplate = RestTemplate()
        objectMapper = ObjectMapper()
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
    ): ResponseEntity<String> {
        val messageMap = mapOf(
            "message" to message,
            "title" to title,
            "priority" to priority,
        )

        val entity = HttpEntity(
            objectMapper.writeValueAsString(messageMap),
            headers,
        )

        val exchange = restTemplate.exchange(
            "$host/message",
            HttpMethod.POST,
            entity,
            String::class.java,
        )
        log.debug("Push message to Gotify: $title")
        return exchange
    }

}
