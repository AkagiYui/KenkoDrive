package com.akagiyui.drive.model.request.folder

/**
 * 创建文件夹 请求实体
 *
 * @author AkagiYui
 */
class CreateFolderRequest {
    /**
     * 文件夹名
     */
    lateinit var name: String

    /**
     * 父文件夹ID
     */
    var parent: String? = null
}
