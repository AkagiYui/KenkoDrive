package com.akagiyui.drive.model.response

/**
 * 文件夹内容 响应实体
 *
 * @author AkagiYui
 */
data class FolderContentResponse(
    /**
     * 文件列表
     */
    val files: List<UserFileResponse>,
    /**
     * 文件夹列表
     */
    val folders: List<FolderResponse>,
    /**
     * 文件夹链
     */
    val folderChain: List<FolderResponse>,
)
