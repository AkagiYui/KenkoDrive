package com.akagiyui.drive.model.response.upload

import com.akagiyui.drive.entity.cache.UploadTask

/**
 * 上传任务信息响应
 * @author AkagiYui
 */
data class UploadTaskResponse(
    val id: String,
    val merged: Boolean,
) {
    constructor(task: UploadTask) : this(
        id = task.id,
        merged = task.merged
    )
}
