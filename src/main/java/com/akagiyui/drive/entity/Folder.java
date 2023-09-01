package com.akagiyui.drive.entity;

import com.akagiyui.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

/**
 * 文件夹 实体
 *
 * @author AkagiYui
 */
@Data
@ToString(exclude = {"parent", "user"})
@Accessors(chain = true)
@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "parent_id", "user_id"})
})
@DynamicInsert
public class Folder extends BaseEntity {
    /**
     * 文件夹名
     */
    @Column(nullable = false)
    private String name;

    /**
     * 父文件夹
     */
    @JoinColumn(name = "parent_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Folder parent;

    /**
     * 所属用户
     */
    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    /**
     * 是否在根目录下
     */
    public boolean inRoot() {
        return parent == null;
    }

    /**
     * 获取完整路径
     */
    public String getPath() {
        if (inRoot()) {
            return "/" + name;
        } else {
            return parent.getPath() + "/" + name;
        }
    }

    /**
     * 获取层级路径列表
     */
    public List<String> getPathList() {
        if (inRoot()) {
            return List.of(name);
        } else {
            List<String> pathList = parent.getPathList();
            pathList.add(name);
            return pathList;
        }
    }
}
