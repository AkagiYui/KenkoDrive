package com.akagiyui.drive.entity;

import com.akagiyui.drive.util.SnowFlakeIdGenerator;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 * @author AkagiYui
 */
@Data
// 继承策略
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// 启用自动填充
@EntityListeners(AuditingEntityListener.class)
// 实体继承映射
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    /**
     * 记录ID
     */
    // 主键生成策略
    @Id
    @GeneratedValue(generator = "snowflakeId")
    @GenericGenerator(
            name = "snowflakeId",
            type = SnowFlakeIdGenerator.class
    )
    String id;

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
