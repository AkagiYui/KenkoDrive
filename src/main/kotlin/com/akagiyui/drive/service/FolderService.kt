package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Folder
import com.akagiyui.drive.model.response.FolderResponse

/**
 * 文件夹 服务接口
 *
 * @author AkagiYui
 */
interface FolderService {
    /**
     * 创建文件夹
     *
     * @param name     文件夹名
     * @param parentId 父文件夹ID
     * @return 创建的文件夹
     */
    fun createFolder(name: String, parentId: String?): Folder

    /**
     * 在根目录下创建文件夹
     *
     * @param name 文件夹名
     * @return 创建的文件夹
     */
    fun createFolder(name: String): Folder

    /**
     * 获取子文件夹
     *
     * @param parentId 父文件夹ID
     * @return 子文件夹列表
     */
    fun getSubFolders(parentId: String?): List<Folder>

    /**
     * 获取文件夹链
     *
     * @param folderId 文件夹ID
     * @return 文件夹链
     */
    fun getFolderChain(folderId: String): List<FolderResponse>

}
