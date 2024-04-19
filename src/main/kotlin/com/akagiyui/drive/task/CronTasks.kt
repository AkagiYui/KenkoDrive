package com.akagiyui.drive.task

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.service.FileInfoService
import com.akagiyui.drive.service.UserFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * 定时任务
 *
 * @author AkagiYui
 */
@Component
class CronTasks @Autowired constructor(
    private val fileInfoService: FileInfoService,
    private val userFileService: UserFileService,
) {
    private val log by LoggerDelegate()

    init {
        log.info("CronTasks initialized")
    }

    /**
     * 删除未使用的文件
     *
     * 每周四凌晨4点执行
     */
    @Scheduled(cron = "0 0 4 ? * 4")
    fun removeUnusedFile() {
        log.info("Start remove unused file")
        // 遍历所有文件，如果没有被引用则删除
        fileInfoService.getAllFileInfo().forEach { fileInfo: FileInfo ->
            if (!userFileService.existByFileId(fileInfo.id!!)) {
                log.info("Remove unused file: ${fileInfo.name}")
                fileInfoService.deleteFile(fileInfo.id!!)
            }
        }
    }
}
