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

    /**
     * 缓存基本对象
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    operator fun <T : Any> set(key: String, value: T) {
        redisTemplate.opsForValue().set(key, value)
    }

    /**
     * 缓存基本对象
     *
     * @param key     缓存的键值
     * @param value   缓存的值
     * @param timeout 时间
     * @param unit    时间单位
     */
    operator fun <T : Any> set(key: String, timeout: Int, unit: TimeUnit, value: T) {
        redisTemplate.opsForValue().set(key, value, timeout.toLong(), unit)
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位(默认为秒)
     * @return true=设置成功；false=设置失败
     */
    fun expire(key: String, timeout: Long, unit: TimeUnit? = TimeUnit.SECONDS): Boolean {
        return redisTemplate.expire(key, timeout, unit!!)
    }

    /**
     * 获得基本对象
     *
     * @param key 缓存键值
     * @return 若未找到，返回null
     */
    operator fun <T> get(key: String): T? {
        return redisTemplate.opsForValue().get(key) as T?
    }

    /**
     * 删除单个对象
     */
    fun delete(key: String): Boolean {
        return redisTemplate.delete(key)
    }

    /**
     * 删除集合对象
     *
     * @param keys 多个对象
     * @return 被删除的条目数量
     */
    fun delete(keys: Collection<String>): Long {
        return redisTemplate.delete(keys)
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    operator fun <T> set(key: String, dataList: List<T>): Long? {
        return redisTemplate.opsForList().rightPushAll(key, dataList)
    }

    /**
     * 获得List对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    fun <T> getList(key: String): List<T> {
        return redisTemplate.opsForList().range(key, 0, -1) as List<T>
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
        for (t in dataSet) {
            setOperation.add(t)
        }
        return setOperation
    }

    /**
     * 获得Set
     *
     * @param key 键名
     * @return 缓存的集合
     */
    fun <T> getSet(key: String): Set<T> {
        return redisTemplate.opsForSet().members(key) as Set<T>
    }

    /**
     * 缓存Map
     *
     * @param key     键名
     * @param dataMap Map
     */
    operator fun <T> set(key: String, dataMap: Map<String, T>) {
        redisTemplate.opsForHash<Any, Any>().putAll(key, dataMap)
    }

    /**
     * 获得Map
     *
     * @param key 键名
     * @return Map
     */
    fun <K, V> getMap(key: String): Map<K, V> {
        return redisTemplate.opsForHash<Any, Any>().entries(key) as Map<K, V>
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    operator fun <T : Any> set(key: String, hKey: String, value: T) {
        redisTemplate.opsForHash<Any, Any>().put(key, hKey, value)
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    fun <T> getMapValue(key: String, hKey: String): T? {
        val opsForHash = redisTemplate.opsForHash<String, T>()
        return opsForHash[key, hKey]
    }

    /**
     * 删除Hash中的数据
     *
     * @param key  Redis键
     * @param hkey Hash键
     */
    fun deleteMapValue(key: String, hkey: String) {
        redisTemplate.opsForHash<Any, Any>().delete(key, hkey)
    }

    /**
     * 获取Hash中的多个数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    fun <T : Any> getMapMultiValue(key: String, hKeys: Collection<Any>): List<T> {
        return redisTemplate.opsForHash<Any, Any>().multiGet(key, hKeys) as List<T>
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
}
