package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.cache.UploadTask

/**
 * 上传任务信息响应
 * @author AkagiYui
 */

class UploadTaskResponse(task: UploadTask) {
    val id = task.id
    val merged = task.merged
}
