package com.akagiyui.drive.component

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import org.springframework.stereotype.Component

/**
 * 日志数据库记录器
 *
 * @author AkagiYui
 */
@Component
class DatabaseLogAppender : AppenderBase<ILoggingEvent>() {
    override fun append(p0: ILoggingEvent) {
        // TODO 自定义log处理器，将log信息存储到数据库
        // 不要在此函数内使用logger，否则会导致死循环
        val message: String = p0.message
        val level: String = p0.level.levelStr
        val loggerName: String = p0.loggerName
        val threadName: String = p0.threadName
        val timestamp: Long = p0.timeStamp
        val formattedMessage: String = p0.formattedMessage
        val throwableProxy: String = p0.throwableProxy?.toString() ?: ""
        val callerData: String = p0.callerData?.contentToString() ?: ""
        val contextName: String = p0.loggerContextVO?.name ?: ""
    }

}
