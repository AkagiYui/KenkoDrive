package com.akagiyui.drive.service

import com.akagiyui.drive.entity.ActionLog
import org.springframework.data.domain.Page
import org.springframework.scheduling.annotation.Async

/**
 * 操作日志服务接口
 *
 * @author AkagiYui
 */
interface ActionLogService {

    /**
     * 记录操作日志
     *
     * @param operator 操作者
     * @param type 操作类型
     * @param action 操作对象
     */
    @Async
    fun log(operator: String, type: String, action: String)

    /**
     * 分页查询操作日志
     *
     * @param index 页码
     * @param size 每页大小
     * @return 操作日志分页
     */
    fun find(index: Int, size: Int): Page<ActionLog>

}
