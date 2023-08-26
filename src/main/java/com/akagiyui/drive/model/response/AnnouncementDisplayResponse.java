package com.akagiyui.drive.model.response;

import com.akagiyui.drive.entity.Announcement;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公告信息 响应
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class AnnouncementDisplayResponse {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 发布者昵称
     */
    private String userNickname;

    /**
     * 发布时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 从公告实体转换
     */
    public static AnnouncementDisplayResponse fromAnnouncement(Announcement announcement) {
        return new AnnouncementDisplayResponse()
                .setTitle(announcement.getTitle())
                .setContent(announcement.getContent())
                .setUserNickname(announcement.getAuthor().getNickname())
                .setCreateTime(announcement.getCreateTime())
                .setUpdateTime(announcement.getUpdateTime());
    }

    /**
     * 从公告实体列表转换
     */
    public static List<AnnouncementDisplayResponse> fromAnnouncementList(List<Announcement> announcementList) {
        return announcementList.stream()
                .map(AnnouncementDisplayResponse::fromAnnouncement)
                .collect(Collectors.toList());
    }
}
