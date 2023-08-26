package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 公告 操作接口
 *
 * @author AkagiYui
 */
public interface AnnouncementRepository extends JpaRepository<Announcement, String> {

    /**
     * 查询所有启用的公告
     */
    List<Announcement> findAnnouncementsByEnabledIsTrue();

    /**
     * 查询所有启用的公告并按更新时间倒序排序
     */
    List<Announcement> findAnnouncementsByEnabledIsTrueOrderByUpdateTimeDesc();

}
