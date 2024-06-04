package com.akagiyui.drive.model.response.announcement

import com.akagiyui.drive.entity.Announcement

/**
 * 公告信息 响应
 *
 * @author AkagiYui
 */
data class AnnouncementResponse(
    /**
     * 公告ID
     */
    val id: String,

    /**
     * 标题
     */
    val title: String,

    /**
     * 内容
     */
    val content: String?,

    /**
     * 用户ID
     */
    val userId: String,

    /**
     * 用户名
     */
    val username: String?,

    /**
     * 已启用
     */
    val enabled: Boolean,

    /**
     * 发布时间
     */
    val createTime: Long,

    /**
     * 修改时间
     */
    val updateTime: Long,
) {
    constructor(announcement: Announcement) : this(
        id = announcement.id,
        title = announcement.title,
        content = announcement.content,
        userId = announcement.author.id,
        username = announcement.author.username,
        enabled = announcement.enabled,
        createTime = announcement.createTime.time,
        updateTime = announcement.updateTime.time
    )
}

/**
 * 从公告实体列表转换
 */
fun List<Announcement>.toResponse(): List<AnnouncementResponse> {
    return this.map { AnnouncementResponse(it) }.toList()
}
