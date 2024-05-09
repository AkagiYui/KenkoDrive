package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.Folder
import com.akagiyui.drive.entity.UserFile
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 用户文件关联 操作接口
 *
 * @author AkagiYui
 */
interface UserFileRepository : JpaRepository<UserFile, String> {

    /**
     * 根据用户ID、文件信息ID、文件夹判断用户文件是否存在
     *
     * @param userId     用户ID
     * @param fileInfoId 文件信息ID
     * @param folder     文件夹
     * @return 用户文件是否存在
     */
    fun existsByUserIdAndFileInfoIdAndFolder(userId: String, fileInfoId: String, folder: Folder?): Boolean

    /**
     * 根据用户ID、文件名、文件夹判断用户文件是否存在
     *
     * @param userId 用户ID
     * @param name   文件名
     * @param folder 文件夹
     * @return 用户文件是否存在
     */
    fun existsByUserIdAndNameAndFolder(userId: String, name: String, folder: Folder?): Boolean

    /**
     * 根据用户ID、文件夹ID获取用户文件列表
     *
     * @param userId   用户ID
     * @param folderId 文件夹ID
     * @return 用户文件列表
     */
    fun findByUserIdAndFolderId(userId: String, folderId: String?): List<UserFile>

    /**
     * 根据文件信息ID判断用户文件是否存在
     *
     * @param fileInfoId 文件信息ID
     * @return 用户文件是否存在
     */
    fun existsByFileInfoId(fileInfoId: String): Boolean

    /**
     * 根据用户ID、ID获取用户文件
     *
     * @param userId 用户ID
     * @param id     ID
     * @return 用户文件
     */
    fun findByUserIdAndId(userId: String, id: String): UserFile?

}
