package com.akagiyui.drive.controller.persist

import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.service.SystemService
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.context.request.async.AsyncRequestNotUsableException
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.CopyOnWriteArrayList


/**
 * 内存信息 Server-Sent Events 处理器
 * @author AkagiYui
 */
@Controller
class MemorySseController(private val systemService: SystemService) : InitializingBean, DisposableBean {
    val sseEmitters = CopyOnWriteArrayList<SseEmitter>()

    @GetMapping("/system/memory/sse")
    @RequirePermission(Permission.SYSTEM_INFO_GET)
    fun sse(): SseEmitter {
        val sseEmitter = SseEmitter()
        sseEmitters.add(sseEmitter)
        sseEmitter.onCompletion { sseEmitters.remove(sseEmitter) }
        sseEmitter.send(systemService.getMemoryInfoHistory()) // 发送历史数据
        return sseEmitter
    }

    private fun broadcast(obj: Any) {
        sseEmitters.forEach {
            try {
                it.send(obj)
            } catch (e: AsyncRequestNotUsableException) {
                // 客户端已断开连接
            }
        }
    }

    override fun afterPropertiesSet() {
        systemService.addRealTimeInfoCallback(::broadcast)
    }

    override fun destroy() {
        systemService.removeRealTimeInfoCallback(::broadcast)
        val sseEmitters = this.sseEmitters.toList()
        for (sseEmitter in sseEmitters) {
            sseEmitter.complete()
        }
    }
}
