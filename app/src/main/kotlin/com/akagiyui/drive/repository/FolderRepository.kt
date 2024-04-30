package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.Folder
import org.springframework.data.jpa.repository.JpaRepository

/**
 * 文件夹表 操作接口
 *
 * @author AkagiYui
 */
interface FolderRepository : JpaRepository<Folder, String> {
    /**
     * 根据父文件夹ID获取子文件夹列表
     *
     * @param parentId 父文件夹ID
     * @return 子文件夹列表
     */
    fun findByUserIdAndParentId(userId: String, parentId: String?): List<Folder>

    /**
     * 根据文件夹名、用户ID、父文件夹ID判断文件夹是否存在
     *
     * @param name 文件夹名
     * @param userId 用户ID
     * @param parentId 父文件夹ID
     * @return 文件夹是否存在
     */
    fun existsByNameAndUserIdAndParentId(name: String, userId: String, parentId: String?): Boolean
}
