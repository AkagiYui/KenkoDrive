package com.akagiyui.drive.task;

import com.akagiyui.drive.service.FileInfoService;
import com.akagiyui.drive.service.UserFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 删除未使用文件 定时任务
 *
 * @author AkagiYui
 */
@Component
@Slf4j
public class RemoveUnusedFileTask {
    private final FileInfoService fileInfoService;
    private final UserFileService userFileService;

    public RemoveUnusedFileTask(FileInfoService fileInfoService, UserFileService userFileService) {
        this.fileInfoService = fileInfoService;
        this.userFileService = userFileService;
    }

    /**
     * 每周四凌晨4点执行
     */
    @Scheduled(cron = "0 0 4 ? * 4")
    public void removeUnusedFile() {
        log.info("Start remove unused file");
        // 遍历所有文件，如果没有被引用则删除
        fileInfoService.getAllFileInfo().forEach(fileInfo -> {
            if (!userFileService.existByFileId(fileInfo.getId())) {
                log.info("Remove unused file: {}", fileInfo.getName());
                fileInfoService.deleteFile(fileInfo.getId());
            }
        });
    }

}
