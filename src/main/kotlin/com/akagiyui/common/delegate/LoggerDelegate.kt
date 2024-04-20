package com.akagiyui.common.delegate

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * slf4j 日志对象获取委托类
 *
 * https://flapypan.top/kotlin-slf4j
 *
 * @author FlapyPan
 */
class LoggerDelegate : ReadOnlyProperty<Any, Logger> {

    /**
     * 延迟创建的单例日志
     */
    private lateinit var _logger: Logger

    /**
     * 获取单例 logger
     */
    override operator fun getValue(thisRef: Any, property: KProperty<*>): Logger {
        if (::_logger.isInitialized) return _logger
        // 获取 logger 对象，由 LoggerFactory 底层保证线程安全
        _logger = LoggerFactory.getLogger(thisRef::class.java)
        return _logger
    }
}
