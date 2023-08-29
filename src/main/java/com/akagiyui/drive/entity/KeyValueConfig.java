package com.akagiyui.drive.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

/**
 * 配置项 实体类
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
@Entity
@Table(name = "system_config")
@EntityListeners(AuditingEntityListener.class)
public class KeyValueConfig {
    /**
     * 配置项键
     * <p>
     * 注意不要使用"key"等关键字
     */
    @Id
    private String configKey;

    /**
     * 配置项值
     * <p>
     * 注意不要使用"value"等关键字
     */
    @Column(nullable = false)
    private String configValue;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(nullable = false)
    private Date updateTime;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Date createTime;
}
