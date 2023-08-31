package com.akagiyui.drive.entity;

import com.akagiyui.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

/**
 * 文件与用户关联 实体类
 *
 * @author AkagiYui
 */
@Data
@ToString(exclude = {"user", "fileInfo"})
@Accessors(chain = true)
@Entity
@Table(name = "user_file", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "file_info_id", "folder_id"})
})
@DynamicInsert
public class UserFile extends BaseEntity {

    /**
     * 所属用户
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 文件信息
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_info_id", nullable = false)
    private FileInfo fileInfo;

    /**
     * 所属文件夹
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    /**
     * 用户文件名（含后缀）
     */
    @Column(nullable = false)
    private String name;

}
