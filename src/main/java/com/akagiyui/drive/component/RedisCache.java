package com.akagiyui.drive.component;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis缓存
 *
 * @author AkagiYui
 */
@SuppressWarnings("unchecked")
@Component
public class RedisCache {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 缓存基本对象
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void set(final String key, final T value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 缓存基本对象
     *
     * @param key     缓存的键值
     * @param value   缓存的值
     * @param timeout 时间
     * @param unit    时间单位
     */
    public <T> void set(final String key, final T value, final Integer timeout, final TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public Boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public Boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获得基本对象
     *
     * @param key 缓存键值
     * @return 若未找到，返回null
     */
    public <T> T get(final String key) {
        return (T)redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除单个对象
     */
    public Boolean delete(final String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param keys 多个对象
     * @return 被删除的条目数量
     */
    public Long delete(final Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> Long set(final String key, final List<T> dataList) {
        return redisTemplate.opsForList().rightPushAll(key, dataList);
    }

    /**
     * 获得List对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getList(final String key) {
        return (List<T>)redisTemplate.opsForList().range(key, 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> set(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = (BoundSetOperations<String, T>)redisTemplate.boundSetOps(key);
        for (T t : dataSet) {
            setOperation.add(t);
        }
        return setOperation;
    }

    /**
     * 获得Set
     *
     * @param key 键名
     * @return 缓存的集合
     */
    public <T> Set<T> getSet(final String key) {
        return (Set<T>)redisTemplate.opsForSet().members(key);
    }

    /**
     * 缓存Map
     *
     * @param key     键名
     * @param dataMap Map
     */
    public <T> void set(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            redisTemplate.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 获得Map
     *
     * @param key 键名
     * @return Map
     */
    public <K, V> Map<K, V> getMap(final String key) {
        return (Map<K, V>)redisTemplate.opsForHash().entries(key);
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setMapValue(final String key, final String hKey, final T value) {
        redisTemplate.opsForHash().put(key, hKey, value);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key  Redis键
     * @param hkey Hash键
     */
    public void delMapValue(final String key, final String hkey) {
        redisTemplate.opsForHash().delete(key, hkey);
    }

    /**
     * 获取Hash中的多个数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMapMultiValue(final String key, final Collection<Object> hKeys) {
        return (List<T>)redisTemplate.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 获得键名列表
     *
     * @param pattern 匹配模板
     * @return 键名列表
     */
    public Set<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 获得键名列表
     *
     * @return 键名列表
     */
    public Set<String> keys() {
        return keys("*");
    }

    /**
     * 键名是否存在
     *
     * @param key 键名
     * @return true=存在；false=不存在
     */
    public Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }
}
