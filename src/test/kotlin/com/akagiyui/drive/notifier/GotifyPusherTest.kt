package com.akagiyui.drive.notifier

import com.akagiyui.common.notifier.GotifyPusher
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.client.ResourceAccessException

/**
 * Gotify 推送测试
 *
 * @author AkagiYui
 */
@SpringBootTest
class GotifyPusherTest {

    @Test
    @Disabled
    fun sendMessage() {
        val gotifyPusher = GotifyPusher(
            "https://gotify.server",
            "application token",
        )

        try {
            println(
                gotifyPusher.push(
                    "这是标题",
                    "这是消息内容",
                    7,
                ),
            )
        } catch (e: ResourceAccessException) {
            println(e.message)
        }
    }
}
