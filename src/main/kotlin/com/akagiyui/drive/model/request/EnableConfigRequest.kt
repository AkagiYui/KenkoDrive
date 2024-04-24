package com.akagiyui.drive.model.request

import jakarta.validation.constraints.NotNull

/**
 * 开关配置 请求
 * @author AkagiYui
 */
class EnableConfigRequest {
    /**
     * 是否启用
     */
    @NotNull
    var enabled: Boolean = false
}
