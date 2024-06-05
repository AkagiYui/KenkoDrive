package com.akagiyui.drive.service

import com.akagiyui.drive.entity.Sharing
import com.akagiyui.drive.entity.User

/**
 * 分享资源 服务
 * @author AkagiYui
 */
interface SharingService {

    /**
     * 创建分享
     * @param user 用户
     * @param userFileId 用户文件ID
     */
    fun createSharing(user: User, userFileId: String): Sharing

    /**
     * 删除分享
     * @param userId 用户ID
     * @param sharingId 分享ID
     */
    fun deleteSharing(userId: String, sharingId: String)

    /**
     * 获取分享列表
     * @param userId 用户ID
     */
    fun list(userId: String): List<Sharing>

    /**
     * 获取分享
     * @param sharingId 分享ID
     */
    fun getSharing(sharingId: String): Sharing?

    /**
     * 根据用户文件ID获取分享
     * @param userFileId 用户文件ID
     * @param userId 用户ID
     */
    fun getSharingByUserFile(userFileId: String, userId: String): Sharing?
}
