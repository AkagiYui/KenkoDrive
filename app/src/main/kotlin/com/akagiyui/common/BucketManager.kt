package com.akagiyui.common

import com.akagiyui.common.delegate.LoggerDelegate
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketListener
import kotlinx.coroutines.*
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 桶管理器
 * @author AkagiYui
 */
class BucketManager(private val idleTime: Long = 1000 * 60 * 5) {
    companion object {
        private const val BUCKET_CLOSED_MESSAGE = "BucketManager is closed"
    }

    private val log by LoggerDelegate()

    /**
     * 桶缓存
     */
    private val bucketMap = ConcurrentHashMap<String, Bucket>()

    /**
     * 访问时间缓存
     */
    private val accessTimeMap = ConcurrentHashMap<String, Long>()

    /**
     * 是否已关闭
     */
    private val isClosed = AtomicBoolean(false)

    /**
     * 清理协程
     */
    private val cleanerScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    val closed get() = isClosed.get()

    init {
        cleanerScope.launch {
            delay(1000 * 60)
            while (!isClosed.get()) {
                val currentTime = System.currentTimeMillis()
                val keysToRemove = accessTimeMap.filter { (_, value) ->
                    (currentTime - value) > idleTime
                }.keys
                keysToRemove.forEach { key ->
                    log.debug("Remove bucket: {}", key)
                    bucketMap.remove(key)
                    accessTimeMap.remove(key)
                }
                delay(1000 * 60) // 1分钟清理一次
            }
        }
    }

    /**
     * 获取桶
     *
     * @param key 键
     * @param refillPerSecond 每秒填充速率
     */
    operator fun get(key: String, refillPerSecond: Long): Bucket {
        check(!isClosed.get()) { BUCKET_CLOSED_MESSAGE }
        return bucketMap.computeIfAbsent(key) {
            log.debug("Create bucket: {}, refillPerSecond: {}", key, refillPerSecond)
            Bucket.builder()
                .addLimit {
                    it.capacity(refillPerSecond)
                        .refillGreedy(refillPerSecond, Duration.ofSeconds(1))
                        .initialTokens(refillPerSecond)
                }
                .withListener(BandwidthBucketListener(key, accessTimeMap))
                .build()
        }
    }

    /**
     * 获取桶
     *
     * @param key 键
     * @param refillPerUnit 每单位时间填充速率
     * @param timeUnit 时间单位
     */
    operator fun get(key: String, refillPerUnit: Long, timeUnit: Duration): Bucket {
        check(!isClosed.get()) { BUCKET_CLOSED_MESSAGE }
        return bucketMap.computeIfAbsent(key) {
            log.debug("Create bucket: {}, refillPerUnit: {}, timeUnit: {}", key, refillPerUnit, timeUnit)
            Bucket.builder()
                .addLimit {
                    it.capacity(refillPerUnit)
                        .refillGreedy(refillPerUnit, timeUnit)
                        .initialTokens(refillPerUnit)
                }
                .withListener(BandwidthBucketListener(key, accessTimeMap))
                .build()
        }
    }

    /**
     * 关闭管理器
     */
    fun close() {
        check(!isClosed.get()) { BUCKET_CLOSED_MESSAGE }
        isClosed.set(true)
        cleanerScope.cancel()
        bucketMap.clear()
        accessTimeMap.clear()
    }

    class BandwidthBucketListener(
        private val key: String,
        private val accessTimeMap: ConcurrentHashMap<String, Long>,
    ) : BucketListener {
        override fun onConsumed(tokens: Long) {
            accessTimeMap[key] = System.currentTimeMillis()
        }

        override fun onRejected(tokens: Long) {
            // DO NOTHING
        }

        override fun onParked(nanos: Long) {
            // DO NOTHING
        }

        override fun onInterrupted(e: InterruptedException?) {
            // DO NOTHING
        }

        override fun onDelayed(nanos: Long) {
            // DO NOTHING
        }
    }
}
