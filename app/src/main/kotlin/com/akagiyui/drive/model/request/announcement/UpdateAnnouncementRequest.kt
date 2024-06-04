package com.akagiyui.drive.model.request.announcement

import jakarta.validation.constraints.Size

/**
 * 更新公告 请求
 *
 * @author AkagiYui
 */
class UpdateAnnouncementRequest {
    /**
     * 公告标题
     */
    @Size(min = 1, max = 255)
    var title: String? = null

    /**
     * 公告内容
     */
    @Size(min = 1)
    var content: String? = null
}
