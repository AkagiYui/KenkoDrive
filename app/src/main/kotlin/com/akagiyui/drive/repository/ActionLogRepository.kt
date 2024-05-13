package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.ActionLog
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * 操作日志 操作接口
 * @author AkagiYui
 */
interface ActionLogRepository : JpaRepository<ActionLog, String>, JpaSpecificationExecutor<ActionLog> {

    /**
     * 查询所有操作日志并按时间倒序
     * @param pageable 分页信息
     * @return 操作日志分页
     */
    fun findAllByOrderByCreateTimeDesc(pageable: Pageable): Page<ActionLog>
}
