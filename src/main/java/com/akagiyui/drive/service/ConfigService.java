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

}
