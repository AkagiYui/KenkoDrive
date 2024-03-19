package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.Announcement;

import java.util.List;

/**
 * 公告服务接口
 *
 * @author AkagiYui
 */
public interface AnnouncementService {

    /**
     * 新增公告
     */
    void addAnnouncement(Announcement announcement);

    /**
     * 获取公告列表
     *
     * @param all 是否获取所有公告
     */
    List<Announcement> getAnnouncementList(boolean all);

    /**
     * 获取用于首页展示的公告列表
     */
    List<Announcement> getAnnouncementDisplayList();

}
