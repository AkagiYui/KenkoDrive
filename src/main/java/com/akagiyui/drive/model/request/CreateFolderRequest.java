package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建文件夹 请求实体
 *
 * @author AkagiYui
 */
@Data
public class CreateFolderRequest {
    /**
     * 文件夹名
     */
    @NotBlank
    private String name;

    /**
     * 父文件夹ID
     */
    private String parent;
}
