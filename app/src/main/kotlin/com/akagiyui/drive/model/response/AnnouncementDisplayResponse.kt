package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Announcement
import java.util.*

/**
 * 公告信息 响应
 *
 * @author AkagiYui
 */
data class AnnouncementDisplayResponse(
    /**
     * 标题
     */
    val title: String,

    /**
     * 内容
     */
    val content: String?,

    /**
     * 发布者昵称
     */
    val userNickname: String,

    /**
     * 发布时间
     */
    val createTime: Date,

    /**
     * 修改时间
     */
    val updateTime: Date,
) {
    constructor(announcement: Announcement) : this(
        title = announcement.title,
        content = announcement.content,
        userNickname = announcement.author.nickname ?: "",
        createTime = announcement.createTime,
        updateTime = announcement.updateTime
    )
}


/**
 * 从公告实体列表转换
 */
fun List<Announcement>.toDisplayResponse(): List<AnnouncementDisplayResponse> {
    return this.map { AnnouncementDisplayResponse(it) }.toList()
}
