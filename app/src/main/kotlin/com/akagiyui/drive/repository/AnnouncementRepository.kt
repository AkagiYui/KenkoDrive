package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.Announcement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * 公告 操作接口
 *
 * JpaRepository不需要@Repository注解，因为JpaRepository接口有@NoRepositoryBean注解，
 * 该注解表示不会被Spring Data JPA实例化，会把它的方法注入到继承它的接口中。
 *
 * @author AkagiYui
 */
interface AnnouncementRepository : JpaRepository<Announcement, String>, JpaSpecificationExecutor<Announcement> {

    /**
     * 查询所有启用的公告
     */
    fun findAnnouncementsByEnabledIsTrue(): List<Announcement>

    /**
     * 查询所有启用的公告并按更新时间倒序排序
     */
    fun findAnnouncementsByEnabledIsTrueOrderByUpdateTimeDesc(): List<Announcement>

}
