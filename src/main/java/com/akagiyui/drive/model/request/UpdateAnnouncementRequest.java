package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新公告 请求
 *
 * @author AkagiYui
 */
@Data
public class UpdateAnnouncementRequest {
    /**
     * 公告标题
     */
    @Size(min = 1, max = 255)
    private String title;
    /**
     * 公告内容
     */
    @Size(min = 1)
    private String content;
}
