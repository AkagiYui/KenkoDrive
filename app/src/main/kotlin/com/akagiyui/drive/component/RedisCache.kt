package com.akagiyui.drive.component

import org.springframework.data.redis.core.BoundSetOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * Redis缓存
 *
 * @author AkagiYui
 */
@Suppress("UNCHECKED_CAST", "ReplaceGetOrSet")
@Component
class RedisCache(private val redisTemplate: RedisTemplate<String, Any?>) {
    private val opsForValue = redisTemplate.opsForValue()
    private val opsForList = redisTemplate.opsForList()
    private val opsForSet = redisTemplate.opsForSet()
    private val opsForHash = redisTemplate.opsForHash<Any, Any>()

    /**
     * 缓存数据
     *
     * @param key     缓存的键值
     * @param timeout 时间
     * @param unit    时间单位
     * @param value   缓存的值
     */
    operator fun <T : Any> set(key: String, timeout: Long? = null, unit: TimeUnit = TimeUnit.SECONDS, value: T) {
        if (timeout != null) {
            opsForValue.set(key, value, timeout, unit)
        } else {
            opsForValue.set(key, value)
        }
    }

    /**
     * 获得基本对象
     *
     * @param key 缓存键值
     * @return 若未找到，返回null
     */
    operator fun <T> get(key: String): T? {
        return opsForValue.get(key) as T?
    }

    /**
     * 删除对象
     * @param key 键名
     * @return 删除的数量
     */
    fun delete(vararg key: String): Long {
        return redisTemplate.delete(key.toList())
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    operator fun <T> set(key: String, dataList: List<T>): Long? {
        return opsForList.rightPushAll(key, dataList)
    }

    /**
     * 获得List对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    fun <T> getList(key: String): List<T> {
        return opsForList.range(key, 0, -1) as List<T>
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    operator fun <T> set(key: String, dataSet: Set<T>): BoundSetOperations<String, T> {
        val setOperation = redisTemplate.boundSetOps(key) as BoundSetOperations<String, T>
        dataSet.forEach { setOperation.add(it) }
        return setOperation
    }

    /**
     * 获得Set
     *
     * @param key 键名
     * @return 缓存的集合
     */
    fun <T> getSet(key: String): Set<T> {
        return opsForSet.members(key) as Set<T>
    }

    /**
     * 缓存Map
     *
     * @param key     键名
     * @param dataMap Map
     */
    operator fun <K, V> set(key: String, dataMap: Map<K, V>) {
        opsForHash.putAll(key, dataMap)
    }

    /**
     * 获得Map
     *
     * @param key 键名
     * @return Map
     */
    fun <K, V> getMap(key: String): Map<K, V> {
        return opsForHash.entries(key) as Map<K, V>
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    operator fun <K : Any, V : Any> set(key: String, hKey: K, value: V) {
        opsForHash.put(key, hKey, value)
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    fun <K : Any, V> getMapValue(key: String, hKey: K): V? {
        val opsForHash = redisTemplate.opsForHash<K, V>()
        return opsForHash.get(key, hKey)
    }

    /**
     * 删除Hash中的数据
     *
     * @param key  Redis键
     * @param hkey Hash键
     */
    fun <K : Any> deleteMapValue(key: String, hkey: K) {
        opsForHash.delete(key, hkey)
    }

    /**
     * 获取Hash中的多个数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    fun <K : Any, V> getMapMultiValue(key: String, hKeys: Collection<K>): List<V> {
        return opsForHash.multiGet(key, hKeys) as List<V>
    }

    /**
     * 获得键名列表
     *
     * @param pattern 匹配模板
     * @return 键名列表
     */
    fun keys(pattern: String = "*"): Set<String> {
        return redisTemplate.keys(pattern)
    }

    /**
     * 键名是否存在
     *
     * @param key 键名
     * @return true=存在；false=不存在
     */
    operator fun contains(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }

    /**
     * 设置过期时间
     *
     * @param key     键名
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    fun expire(key: String, timeout: Long, unit: TimeUnit) {
        redisTemplate.expire(key, timeout, unit)
    }
}
