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
    }

}
