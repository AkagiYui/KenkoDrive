package com.akagiyui.drive.model.request

import com.akagiyui.drive.entity.Announcement
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 新增公告 请求
 *
 * @author AkagiYui
 */
class AddAnnouncementRequest {
    /**
     * 标题
     */
    @NotBlank(message = "{title cannot be empty}")
    @NotNull(message = "{title is missing}")
    lateinit var title: String

    /**
     * 内容
     */
    lateinit var content: String

    /**
     * 转换为公告实体
     */
    fun toAnnouncement(): Announcement {
        return Announcement().also {
            it.title = title
            it.content = content
            it.enabled = true
        }
    }
}
