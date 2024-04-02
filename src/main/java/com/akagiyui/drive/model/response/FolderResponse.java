package com.akagiyui.drive.model.response;

import com.akagiyui.drive.entity.Folder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 文件夹信息 响应实体
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class FolderResponse {
    /**
     * 文件夹ID
     */
    private String id;

    /**
     * 文件夹名
     */
    private String name;

    /**
     * 创建时间
     */
    private long createTime;

    public FolderResponse(Folder folder) {
        this.id = folder.getId();
        this.name = folder.getName();
        this.createTime = folder.getCreateTime().getTime();
    }

    /**
     * 将文件夹列表转换为文件夹响应列表
     *
     * @param folders 文件夹 列表
     * @return 文件夹响应 列表
     */
    public static List<FolderResponse> fromFolderList(List<Folder> folders) {
        return folders.stream().map(FolderResponse::new).toList();
    }
}

