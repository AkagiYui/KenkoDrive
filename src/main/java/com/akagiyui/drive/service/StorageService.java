package com.akagiyui.drive.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;

import java.io.OutputStream;

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
     * 保存文件，请手动关闭输出流
     *
     * @param key 文件名
     * @return 文件输出流
     */
    OutputStream saveFile(String key);

    /**
     * 保存文件
     *
     * @param key       文件名
     * @param overwrite 是否覆盖
     * @return 文件输出流
     */
    OutputStream saveFile(String key, boolean overwrite);

    /**
     * 文件是否存在
     */
    boolean exists(String key);

    /**
     * 删除文件
     */
    @Async
    void deleteFile(String key);
}
