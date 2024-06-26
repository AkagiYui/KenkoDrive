package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Folder
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.response.folder.FolderResponse

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
    fun createFolder(user: User, name: String, parentId: String? = null): Folder

    /**
     * 获取子文件夹
     *
     * @param parentId 父文件夹ID
     * @return 子文件夹列表
     */
    fun getSubFolders(userId: String, parentId: String?): List<Folder>

    /**
     * 获取文件夹链
     *
     * @param folderId 文件夹ID
     * @return 文件夹链
     */
    fun getFolderChain(userId: String, folderId: String): List<FolderResponse>

    /**
     * 获取文件夹
     *
     * @param folderId 文件夹ID
     * @return 文件夹
     */
    fun getFolderById(folderId: String): Folder

    /**
     * 删除文件夹
     *
     * @param folderId 文件夹ID
     */
    fun deleteFolder(userId: String, folderId: String)

    /**
     * 移动文件夹
     */
    fun moveFolder(userId: String, folderId: String, parentId: String?)

    /**
     * 重命名文件夹
     * @param userId 用户ID
     * @param folderId 文件夹ID
     * @param newName 新文件夹名
     */
    fun rename(userId: String, folderId: String, newName: String)
}
