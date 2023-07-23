package com.akagiyui.drive.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;

/**
 * 存储服务接口
 *
 * @author AkagiYui
 */
public interface StorageService {
    /**
     * 获取文件
     */
     InputStreamResource getFile(String key);

    /**
    * 保存文件
    */
    @Async
    void saveFile(String key, byte[] content);

    /**
     * 文件是否存在
     */
    boolean exists(String key);
}
