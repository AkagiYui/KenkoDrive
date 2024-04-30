package com.akagiyui.drive.model.response

import com.akagiyui.drive.entity.Folder

/**
 * 文件夹信息 响应实体
 *
 * @author AkagiYui
 */
class FolderResponse() {
    /**
     * 文件夹ID
     */
    var id: String? = null

    /**
     * 文件夹名
     */
    var name: String? = null

    /**
     * 创建时间
     */
    var createTime: Long = 0

    constructor(folder: Folder) : this() {
        this.id = folder.id
        this.name = folder.name
        this.createTime = folder.createTime.time
    }

    companion object {
        /**
         * 将文件夹列表转换为文件夹响应列表
         *
         * @param folderList 文件夹 列表
         * @return 文件夹响应 列表
         */
        fun fromFolderList(folderList: List<Folder>): List<FolderResponse> {
            return folderList.stream()
                .map { folder -> FolderResponse(folder) }
                .toList()
        }
    }
}
