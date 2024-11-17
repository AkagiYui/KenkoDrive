package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.BusinessException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.entity.Announcement
import com.akagiyui.drive.entity.Announcement_
import com.akagiyui.drive.model.AnnouncementFilter
import com.akagiyui.drive.model.request.announcement.UpdateAnnouncementRequest
import com.akagiyui.drive.repository.AnnouncementRepository
import com.akagiyui.drive.service.AnnouncementService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service


/**
 * 公告服务实现类
 *
 * @author AkagiYui
 */
@Service
class AnnouncementServiceImpl(private val announcementRepository: AnnouncementRepository) : AnnouncementService {

    /**
     * 根据ID查找公告或抛出异常
     *
     * @param id 公告ID
     * @return 公告实体
     */
    private fun getAnnouncement(id: String): Announcement {
        return announcementRepository.findById(id).orElseThrow {
            BusinessException(ResponseEnum.NOT_FOUND)
        }
    }

    override fun addAnnouncement(announcement: Announcement): Announcement {
        return announcementRepository.save(announcement)
    }

    override fun getAnnouncementList(all: Boolean): List<Announcement> {
        return if (all) announcementRepository.findAll()
        else announcementRepository.findAnnouncementsByEnabledIsTrue()
    }

    override fun getAnnouncementDisplayList(): List<Announcement> {
        return announcementRepository.findAnnouncementsByEnabledIsTrueOrderByUpdateTimeDesc()
    }

    override fun find(index: Int, size: Int, filter: AnnouncementFilter?): Page<Announcement> {
        val pageable = PageRequest.of(index, size)

        // 条件查询
        val specification = Specification { root, _, cb ->
            filter?.expression.hasText {
                val likePattern = "%$it%"
                val titlePredicate = cb.like(root[Announcement_.title], likePattern)
                val contentPredicate = cb.like(root[Announcement_.content], likePattern)
                cb.or(titlePredicate, contentPredicate)
            }
        }

        return announcementRepository.findAll(specification, pageable)
    }

    override fun disable(id: String, disabled: Boolean) {
        val announcement = getAnnouncement(id)
        announcement.enabled = !disabled
        announcementRepository.save(announcement)
    }

    override fun delete(id: String) {
        val announcement = getAnnouncement(id)
        announcementRepository.delete(announcement)
    }

    override fun update(id: String, request: UpdateAnnouncementRequest) {
        val announcement = getAnnouncement(id)
        request.title.hasText { announcement.title = it }
        request.content.hasText { announcement.content = it }
        announcementRepository.save(announcement)
    }
}
