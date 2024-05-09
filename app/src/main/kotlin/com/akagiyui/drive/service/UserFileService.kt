package com.akagiyui.drive.service

import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile

/**
 * 用户文件关联 服务接口
 *
 * @author AkagiYui
 */
interface UserFileService {
    /**
     * 添加关联
     */
    fun addAssociation(user: User, fileInfo: FileInfo, folderId: String? = null): UserFile

    /**
     * 获取文件夹下的文件
     * @param folderId 文件夹ID
     */
    fun getFiles(folderId: String?): List<UserFile>

    /**
     * 判断文件是否有用户关联
     */
    fun existByFileId(fileId: String): Boolean

    /**
     * 获取用户文件
     */
    fun getUserFileById(id: String): UserFile

    /**
     * 获取临时文件ID
     */
    fun getTemporaryId(userFileId: String): String

    /**
     * 根据临时文件ID获取文件信息
     */
    fun getFileInfoByTemporaryId(temporaryId: String): UserFile

    /**
     * 删除用户文件
     */
    fun userDeleteFile(id: String)
}
