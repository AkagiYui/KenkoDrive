package com.akagiyui.drive.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 公告查询筛选
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class AnnouncementFilter {
    /**
     * 表达式
     */
    private String expression;
}
