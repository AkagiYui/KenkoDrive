package com.akagiyui.drive.service;

/**
 * 配置 服务接口
 *
 * @author AkagiYui
 */
public interface ConfigService {

    /**
     * 是否开放注册 键名
     */
    String REGISTER_ENABLED = "registerEnabled";

    /**
     * 是否初始化
     */
    boolean isRegisterEnabled();

    /**
     * 设置 是否开放注册
     */
    boolean setRegisterEnabled(boolean enabled);

    /**
     * 是否初始化 键名
     */
    String IS_INITIALIZED = "isInitialized";

    /**
     * 是否初始化
     */
    boolean isInitialized();

    /**
     * 设置 是否初始化
     */
    boolean setInitialized(boolean initialized);

    /**
     * 文件分片大小 键名
     */
    String FILE_UPLOAD_CHUNK_SIZE = "fileUploadChunkSize";

    /**
     * 文件分片大小，单位：字节，默认：5MB
     */
    int getFileUploadChunkSize();

    /**
     * 设置 文件分片大小
     */
    int setFileUploadChunkSize(int chunkSize);

    /**
     * 全局文件上传大小限制 键名
     */
    String FILE_UPLOAD_MAX_SIZE = "fileUploadMaxSize";

    /**
     * 全局文件上传大小限制，单位：字节，默认：100MB
     */
    long getFileUploadMaxSize();

    /**
     * 设置 全局文件上传大小限制
     */
    long setFileUploadMaxSize(long maxSize);
}
