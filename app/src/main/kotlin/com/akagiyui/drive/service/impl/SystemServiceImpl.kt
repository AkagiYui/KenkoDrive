package com.akagiyui.drive.service.impl

import cn.hutool.core.collection.ConcurrentHashSet
import com.akagiyui.common.collection.CircularBuffer
import com.akagiyui.drive.service.SystemService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

/**
 * 系统服务实现
 * @author AkagiYui
 */
@Service
class SystemServiceImpl : SystemService {

    private val realTimeInfoCallbacks = ConcurrentHashSet<(Map<String, Any>) -> Unit>()
    private val memoryInfoBuffer: CircularBuffer<Map<String, Number>> = CircularBuffer(60)

    /**
     * 定时收集内存信息
     */
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
        realTimeInfoCallbacks.forEach { it(memoryInfo) }
    }

    override fun addRealTimeInfoCallback(callback: (Map<String, Any>) -> Unit) {
        realTimeInfoCallbacks.add(callback)
    }

    override fun removeRealTimeInfoCallback(callback: (Map<String, Any>) -> Unit) {
        realTimeInfoCallbacks.remove(callback)
    }

    override fun getMemoryInfoHistory(): List<Map<String, Number>> {
        return memoryInfoBuffer.getAll()
    }

}
