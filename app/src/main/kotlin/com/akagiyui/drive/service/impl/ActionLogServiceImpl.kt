package com.akagiyui.drive.service.impl

import com.akagiyui.drive.entity.ActionLog
import com.akagiyui.drive.repository.ActionLogRepository
import com.akagiyui.drive.service.ActionLogService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

/**
 * 操作日志服务实现
 *
 * @author AkagiYui
 */
@Service
class ActionLogServiceImpl(
    private val repository: ActionLogRepository,
) : ActionLogService {

    override fun log(operator: String, type: String, action: String) {
        val actionLog = ActionLog().apply {
            this.operator = operator
            this.type = type
            this.action = action
        }
        repository.save(actionLog)
    }

    override fun find(index: Int, size: Int): Page<ActionLog> {
        return repository.findAllByOrderByCreateTimeDesc(PageRequest.of(index, size))
    }

}
