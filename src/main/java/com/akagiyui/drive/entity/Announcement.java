package com.akagiyui.drive.entity;

import com.akagiyui.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;


/**
 * 公告实体
 *
 * @author AkagiYui
 */
@Data
@ToString(exclude = "author")
@Accessors(chain = true)
@Entity
@Table
@DynamicInsert
public class Announcement extends BaseEntity {
    /**
     * 标题
     */
    @Column(nullable = false)
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 已启用
     */
    @Column(nullable = false)
    @ColumnDefault("true")
    private Boolean enabled;

    /**
     * 发布者
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

}
