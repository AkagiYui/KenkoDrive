package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.Announcement;
import com.akagiyui.drive.model.filter.AnnouncementFilter;
import org.springframework.data.domain.Page;

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

    /**
     * 获取公告列表
     *
     * @param index  页码
     * @param size   每页数量
     * @param filter 筛选条件
     */
    Page<Announcement> find(Integer index, Integer size, AnnouncementFilter filter);

    /**
     * 设置公告状态
     *
     * @param id       公告id
     * @param disabled 公告开关
     */
    void disable(String id, boolean disabled);

    /**
     * 删除公告
     *
     * @param id 公告id
     */
    void delete(String id);
}
