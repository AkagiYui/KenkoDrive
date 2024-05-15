package com.akagiyui.common.utils

import oshi.SystemInfo
import oshi.hardware.CentralProcessor.TickType
import java.lang.management.ManagementFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * 系统信息工具类
 * @author AkagiYui
 */

object SystemInformationUtil {
    /**
     * 等待休眠时间，单位ms
     */
    private const val WAIT_TIME_MS: Long = 500
    private val oshiInfo = SystemInfo()

    /**
     * 获取CPU信息
     * 注意：需要等待一段时间才能获取到CPU信息
     *
     * @param testDuration 测试持续时间
     * @return CPU信息
     */
    fun getCpuUsageInformation(testDuration: Long = WAIT_TIME_MS): CpuUsageInformation {
        val processor = oshiInfo.hardware.processor
        // CPU信息
        val previousTicks = processor.systemCpuLoadTicks
        Thread.sleep(testDuration)
        TimeUnit.MILLISECONDS.sleep(testDuration)
        val currentTicks = processor.systemCpuLoadTicks
        val nice = currentTicks[TickType.NICE.index] - previousTicks[TickType.NICE.index]
        val irq = currentTicks[TickType.IRQ.index] - previousTicks[TickType.IRQ.index]
        val softIrq = currentTicks[TickType.SOFTIRQ.index] - previousTicks[TickType.SOFTIRQ.index]
        val steal = currentTicks[TickType.STEAL.index] - previousTicks[TickType.STEAL.index]
        val system = currentTicks[TickType.SYSTEM.index] - previousTicks[TickType.SYSTEM.index]
        val user = currentTicks[TickType.USER.index] - previousTicks[TickType.USER.index]
        val ioWait = currentTicks[TickType.IOWAIT.index] - previousTicks[TickType.IOWAIT.index]
        val idle = currentTicks[TickType.IDLE.index] - previousTicks[TickType.IDLE.index]
        val totalCpu = user + nice + system + idle + ioWait + irq + softIrq + steal
        return CpuUsageInformation(
            processor.logicalProcessorCount,
            totalCpu,
            system,
            user,
            ioWait,
            idle
        )
    }

    /**
     * 获取JVM信息
     */
    fun getJvmInformation(): JvmInformation {
        val props: Properties = System.getProperties()
        val runtime = Runtime.getRuntime()
        // jvm 运行时间
        val startTime = ManagementFactory.getRuntimeMXBean().startTime
        val startLocalTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(startTime),
            ZoneId.systemDefault()
        )
        val uptime = Duration.between(startLocalTime, LocalDateTime.now())

        return JvmInformation(
            props.getProperty("java.version"),
            props.getProperty("java.home"),
            runtime.totalMemory(),
            runtime.freeMemory(),
            runtime.maxMemory(),
            uptime.toMillis()
        )
    }

    /**
     * 获取内存信息
     *
     * @param size  大小单位，默认为B
     * @return      内存信息
     */
    fun getMemoryInformation(size: SizeEnum = SizeEnum.B): MemoryInformation {
        val memory = oshiInfo.hardware.memory
        return MemoryInformation(
            memory.total.toDouble() / size.size,
            memory.available.toDouble() / size.size
        )
    }

    /**
     * 获取服务器信息
     *
     * @return  服务器信息
     */
    fun getSystemInformation(): SystemDetail {
        val props = System.getProperties()
        val os = oshiInfo.operatingSystem
        val computerName = System.getenv("COMPUTERNAME") ?: System.getenv("HOSTNAME")
        return SystemDetail(
            computerName ?: os.networkParams.hostName,
            props.getProperty("user.name"),
            props.getProperty("user.dir"),
            os.toString(),
            props.getProperty("os.arch"),
            props.getProperty("os.version"),
            os.bitness
        )
    }

    fun getHardwareInformation(): HardwareInformation {
        val hardware = oshiInfo.hardware
        val computerSystem = hardware.computerSystem
        return HardwareInformation(
            computerSystem.manufacturer,
            computerSystem.firmware.manufacturer,
            computerSystem.baseboard.manufacturer,
            hardware.processor.toString().split("\n")[0]
        )
    }

    /**
     * 获取网络流量
     * @return  网络流量{网卡名: (发送字节数, 接收字节数)}
     */
    fun getNetworkTraffic(): Map<String, Pair<Long, Long>> {
        val activeInterfaces = oshiInfo.hardware.networkIFs
            .asSequence()
            .filter { !it.queryNetworkInterface().isLoopback }
            .filter { !it.queryNetworkInterface().isPointToPoint }
            .filter { it.queryNetworkInterface().isUp }
            .filter { !it.queryNetworkInterface().isVirtual }
            .filter { it.iPv4addr.isNotEmpty() && it.iPv6addr.isNotEmpty() }
        val result = mutableMapOf<String, Pair<Long, Long>>()
        activeInterfaces.forEach {
            result[it.displayName] = Pair(it.bytesSent, it.bytesRecv)
        }
        return result
    }

    /**
     * 获取磁盘流量
     * @return  磁盘流量{磁盘名: (读取字节数, 写入字节数)}
     */
    fun getDiskTraffic(): Map<String, Pair<Long, Long>> {
        val activeDisks = oshiInfo.hardware.diskStores
        val result = mutableMapOf<String, Pair<Long, Long>>()
        activeDisks.forEach {
            result[it.name] = Pair(it.readBytes, it.writeBytes)
        }
        return result
    }
}

/**
 * CPU使用量信息
 */
data class CpuUsageInformation(
    /**
     * 逻辑处理器(核心数)
     */
    var logicalProcessorCount: Int,
    /**
     * CPU总的可用量
     */
    var totalUsage: Long,
    /**
     * CPU系统使用量
     */
    var systemUsage: Long,
    /**
     * CPU用户使用量
     */
    var userUsage: Long,
    /**
     * CPU当前IO等待用量
     */
    var ioWaitUsage: Long,
    /**
     * CPU当前空闲量
     */
    var idle: Long,
) {
    /**
     * CPU使用率
     */
    val usageRate = (1.0 - idle.toDouble() / totalUsage) * 100
}

/**
 * JVM信息
 */
data class JvmInformation(
    /**
     * Java版本
     */
    val version: String,
    /**
     * JavaHome
     */
    val home: String,
    /**
     * JVM内存总量（byte）
     */
    val totalMemory: Long,
    /**
     * JVM空闲内存（byte）
     */
    val freeMemory: Long,
    /**
     * JVM最大可申请内存（byte）
     */
    val maxMemory: Long,
    /**
     * JVM运行时长（毫秒）
     */
    val uptime: Long,
) {
    /**
     * JVM已使用内存（byte）
     */
    val usedMemory = totalMemory - freeMemory
}

/**
 * 内存信息
 */
data class MemoryInformation(
    /**
     * 内存总量（byte）
     */
    val total: Double,

    /**
     * 剩余内存（byte）
     */
    val free: Double,
) {
    /**
     * 已用内存（byte）
     */
    val used = total - free
}

data class SystemDetail(
    /**
     * 计算机名
     */
    val computerName: String,

    /**
     * 用户名
     */
    val userName: String,

    /**
     * 项目路径
     */
    val userDir: String,

    /**
     * 操作系统
     */
    val osName: String,

    /**
     * 系统架构
     */
    val osArch: String,

    /**
     * 系统版本
     */
    val osVersion: String,

    /**
     * 系统位数
     */
    val osBit: Int,
)


enum class SizeEnum(val size: Long) {
    B(1),

    /**
     * 1KB = 1024B
     */
    KB(1024),

    /**
     * 1MB = 1024KB
     */
    MB(KB.size * 1024),

    /**
     * 1GB = 1024MB
     */
    GB(MB.size * 1024);
}

data class HardwareInformation(
    val computerSystem: String,
    val firmware: String,
    val baseboard: String,
    val processor: String,
)
