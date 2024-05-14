package com.akagiyui.drive.service

import com.akagiyui.drive.model.AvatarContent
import org.springframework.web.multipart.MultipartFile

/**
 * 头像服务接口
 *
 * @author AkagiYui
 */
interface AvatarService {
    /**
     * 获取头像
     * @return 头像
     */
    fun getAvatar(userId: String): AvatarContent

    /**
     * 保存头像
     *
     * @param avatar 头像
     */
    fun saveAvatar(userId: String, avatar: MultipartFile)
}
