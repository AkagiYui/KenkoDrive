package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Announcement

/**
 * 公告信息 响应
 *
 * @author AkagiYui
 */
class AnnouncementResponse() {

    /**
     * 公告ID
     */
    var id: String? = null

    /**
     * 标题
     */
    var title: String? = null

    /**
     * 内容
     */
    var content: String? = null

    /**
     * 用户ID
     */
    var userId: String? = null

    /**
     * 用户名
     */
    var username: String? = null

    /**
     * 已启用
     */
    var enabled = false

    /**
     * 发布时间
     */
    var createTime: Long = 0

    /**
     * 修改时间
     */
    var updateTime: Long = 0

    constructor(announcement: Announcement) : this() {
        this.id = announcement.id
        this.title = announcement.title
        this.content = announcement.content
        this.userId = announcement.author.id
        this.username = announcement.author.username
        this.enabled = announcement.enabled
        this.createTime = announcement.createTime.time
        this.updateTime = announcement.updateTime.time
    }

    companion object {
        /**
         * 从公告实体列表转换
         */
        fun fromAnnouncementList(announcementList: List<Announcement>): List<AnnouncementResponse> {
            return announcementList.stream()
                .map { announcement -> AnnouncementResponse(announcement) }
                .toList()
        }
    }
}
