package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Announcement
import com.akagiyui.drive.model.AnnouncementFilter
import com.akagiyui.drive.model.request.UpdateAnnouncementRequest
import org.springframework.data.domain.Page

/**
 * 公告服务接口
 *
 * @author AkagiYui
 */
interface AnnouncementService {

    /**
     * 新增公告
     *
     * @return 公告id
     */
    fun addAnnouncement(announcement: Announcement): Announcement

    /**
     * 获取公告列表
     *
     * @param all 是否获取所有公告
     */
    fun getAnnouncementList(all: Boolean): List<Announcement>

    /**
     * 获取用于首页展示的公告列表
     */
    fun getAnnouncementDisplayList(): List<Announcement>

    /**
     * 获取公告列表
     *
     * @param index  页码
     * @param size   每页数量
     * @param filter 筛选条件
     */
    fun find(index: Int, size: Int, filter: AnnouncementFilter?): Page<Announcement>

    /**
     * 设置公告状态
     *
     * @param id       公告id
     * @param disabled 公告开关
     */
    fun disable(id: String, disabled: Boolean)

    /**
     * 删除公告
     *
     * @param id 公告id
     */
    fun delete(id: String)

    /**
     * 更新公告
     *
     * @param id      公告id
     * @param request 更新内容
     */
    fun update(id: String, request: UpdateAnnouncementRequest)

}
