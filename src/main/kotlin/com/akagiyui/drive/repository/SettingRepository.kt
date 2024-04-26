package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.KeyValueSetting
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 设置表 操作接口
 *
 * @author AkagiYui
 */
interface SettingRepository : JpaRepository<KeyValueSetting, String> {
    /**
     * 根据设置项键查找设置项
     *
     * @param key 设置项键
     * @return 设置项
     */
    fun findBySettingKey(key: String): KeyValueSetting?
}
