package com.akagiyui.drive.model.request;

import com.akagiyui.drive.entity.Announcement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增公告 请求
 *
 * @author AkagiYui
 */
@Data
public class AddAnnouncementRequest {

    /**
     * 标题
     */
    @NotBlank(message = "{title cannot be empty}")
    @NotNull(message = "{title is missing}")
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 转换为公告实体
     */
    public Announcement toAnnouncement() {
        return new Announcement()
                .setTitle(title)
                .setContent(content)
                .setEnabled(true);
    }

}
