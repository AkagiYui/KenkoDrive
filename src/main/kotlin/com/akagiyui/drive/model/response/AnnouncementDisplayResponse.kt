package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Announcement
import java.util.*

/**
 * 公告信息 响应
 *
 * @author AkagiYui
 */
class AnnouncementDisplayResponse() {
    /**
     * 标题
     */
    var title: String? = null

    /**
     * 内容
     */
    var content: String? = null

    /**
     * 发布者昵称
     */
    var userNickname: String? = null

    /**
     * 发布时间
     */
    var createTime: Date? = null

    /**
     * 修改时间
     */
    var updateTime: Date? = null

    /**
     * 从公告实体转换
     *
     * @param announcement 公告 实体
     */
    constructor(announcement: Announcement) : this() {
        this.title = announcement.title
        this.content = announcement.content
        this.userNickname = announcement.author.nickname
        this.createTime = announcement.createTime
        this.updateTime = announcement.updateTime
    }

    companion object {
        /**
         * 从公告实体列表转换
         */
        fun fromAnnouncementList(announcementList: List<Announcement>): List<AnnouncementDisplayResponse> {
            return announcementList.stream()
                .map { announcement -> AnnouncementDisplayResponse(announcement) }
                .toList()
        }
    }
}
