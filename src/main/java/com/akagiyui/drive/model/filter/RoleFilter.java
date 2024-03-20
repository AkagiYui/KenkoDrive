package com.akagiyui.drive.model.filter;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 角色查询筛选
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class RoleFilter {
    /**
     * 表达式
     */
    private String expression;
}
