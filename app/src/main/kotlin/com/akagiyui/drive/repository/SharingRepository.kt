package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.Sharing
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

/**
 * 分享资源 操作接口
 * @author AkagiYui
 */
interface SharingRepository : JpaRepository<Sharing, String>, JpaSpecificationExecutor<Sharing> {

    /**
     * 根据用户ID查找分享资源
     * @param userId 用户ID
     */
    fun findByUserId(userId: String): List<Sharing>

    /**
     * 根据用户ID、ID查找分享资源
     * @param userId 用户ID
     * @param id 分享ID
     */
    fun findByUserIdAndId(userId: String, id: String): Sharing?

    /**
     * 根据用户ID、文件ID查找分享资源
     * @param userId 用户ID
     * @param userFileId 用户文件ID
     */
    fun findByUserIdAndFileId(userId: String, userFileId: String): Sharing?

}
