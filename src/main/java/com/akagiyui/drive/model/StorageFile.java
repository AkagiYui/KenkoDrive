package com.akagiyui.drive.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 存储中的文件
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class StorageFile {
    /**
     * 文件名
     */
    private String key;

    /**
     * 文件大小
     */
    private long size;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 文件哈希
     */
    private String hash;
}
