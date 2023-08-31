package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.FileInfo;
import com.akagiyui.drive.entity.Folder;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.UserFile;

import java.util.List;

/**
 * 用户文件关联 服务接口
 *
 * @author AkagiYui
 */
public interface UserFileService {
    /**
     * 添加关联
     */
    void addAssociation(User user, FileInfo fileInfo, Folder folder);

    /**
     * 获取文件夹下的文件
     * @param folderId 文件夹ID
     */
    List<UserFile> getFiles(String folderId);
}
