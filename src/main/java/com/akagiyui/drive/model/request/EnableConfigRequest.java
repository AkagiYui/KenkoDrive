package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 开关配置 请求
 * @author AkagiYui
 */
@Data
@NoArgsConstructor
public class EnableConfigRequest {
    @NotNull
    private Boolean enabled;
}
