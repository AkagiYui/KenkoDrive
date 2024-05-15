package com.akagiyui.common.model

import com.akagiyui.drive.model.Permission

/**
 * 带权限的 Bean 接口
 * @author AkagiYui
 */

interface BeanWithPermissions {
    val permissions: Set<Permission>
}
