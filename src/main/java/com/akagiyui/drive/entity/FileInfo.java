package com.akagiyui.drive.entity;

import com.akagiyui.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

/**
 * 文件信息实体
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
@Entity
@Table
@DynamicInsert
public class FileInfo extends BaseEntity {
    /**
     * 文件名
     */
    @Column(nullable = false)
    private String name;

    /**
     * 文件大小（字节Byte）
     */
    @Column(nullable = false)
    private Long size;

    /**
     * 文件类型
     */
    @Column(nullable = false)
    private String type;

    /**
     * 文件哈希
     */
    @Column(nullable = false, unique = true)
    private String hash;

    /**
     * 文件存储Key
     */
    @Column(nullable = false)
    private String storageKey;

    /**
     * 引用次数（防止重复上传）
     */
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long refCount;

    /**
     * 下载次数
     */
    @Column(nullable = false)
    @ColumnDefault("0")
    private Long downloadCount;
}
