package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.Folder;

import java.util.List;

/**
 * 文件夹 服务接口
 *
 * @author AkagiYui
 */
public interface FolderService {
    /**
     * 创建文件夹
     *
     * @param name     文件夹名
     * @param parentId 父文件夹ID
     * @return 创建的文件夹
     */
    Folder createFolder(String name, String parentId);

    /**
     * 在根目录下创建文件夹
     *
     * @param name 文件夹名
     * @return 创建的文件夹
     */
    Folder createFolder(String name);

    /**
     * 获取子文件夹
     *
     * @param parentId 父文件夹ID
     * @return 子文件夹列表
     */
    List<Folder> getSubFolders(String parentId);
}
