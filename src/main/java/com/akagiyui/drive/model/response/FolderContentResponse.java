package com.akagiyui.drive.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 文件夹内容 响应实体
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class FolderContentResponse {
    /**
     * 文件列表
     */
    private List<UserFileResponse> files;

    /**
     * 文件夹列表
     */
    private List<FolderResponse> folders;

    /**
     * 文件夹链
     */
    private List<FolderResponse> folderChain;

    public FolderContentResponse(List<UserFileResponse> files, List<FolderResponse> folders, List<FolderResponse> folderChain) {
        this.files = files;
        this.folders = folders;
        this.folderChain = folderChain;
    }
}
