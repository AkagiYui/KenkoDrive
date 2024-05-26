package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.UserFile

/**
 * 用户文件信息 响应实体
 *
 * @author AkagiYui
 */
class UserFileResponse(userFile: UserFile) {
    /**
     * 文件ID
     */
    val id = userFile.id

    /**
     * 文件名
     */
    val name: String = userFile.name

    /**
     * 文件大小（字节Byte）
     */
    val size = userFile.fileInfo.size

    /**
     * 文件类型
     */
    val type: String = userFile.fileInfo.type

    /**
     * 创建时间
     */
    val createTime = userFile.createTime.time

    /**
     * 文件已被锁定
     */
    val locked = userFile.fileInfo.locked

    companion object {
        /**
         * 将文件列表转换为文件响应列表
         *
         * @param userFiles 文件 列表
         * @return 文件响应 列表
         */
        fun fromUserFileList(userFiles: List<UserFile>): List<UserFileResponse> {
            return userFiles
                .stream()
                .map { UserFileResponse(it) }
                .toList()
        }
    }
}
