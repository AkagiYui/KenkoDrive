package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.KeyValueConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 配置表 操作接口
 *
 * @author AkagiYui
 */
@Repository
public interface ConfigRepository extends JpaRepository<KeyValueConfig, String> {
    /**
     * 根据配置项键查找配置项
     *
     * @param key 配置项键
     * @return 配置项
     */
    Optional<KeyValueConfig> findByConfigKey(String key);
}
