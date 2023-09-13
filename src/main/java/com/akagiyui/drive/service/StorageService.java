package com.akagiyui.drive.service;

import com.akagiyui.drive.model.StorageFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.scheduling.annotation.Async;

import java.io.OutputStream;

/**
 * 存储 服务接口
 * <p>
 *     存储服务是一个 Key-Value 存储服务，Key 为文件名，Value 为文件内容
 *     {key: String, value: byte[]/Stream}
 * </p>
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

    /**
     * 存储分片
     */
    void saveChunk(String userId, String fileHash, int chunkIndex, byte[] content);

    /**
     * 合并分片
     *
     * @return
     */
    StorageFile mergeChunk(String userId, String fileHash, int chunkCount);
}
