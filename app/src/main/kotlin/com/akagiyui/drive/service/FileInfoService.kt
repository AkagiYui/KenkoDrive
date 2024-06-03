package com.akagiyui.drive.service

import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.FileInfoFilter
import org.springframework.data.domain.Page
import org.springframework.web.multipart.MultipartFile
import java.util.stream.Stream

/**
 * 文件信息接口
 *
 * @author AkagiYui
 */
interface FileInfoService {
    /**
     * 获取文件
     */
    fun getFileInfo(id: String): FileInfo

    /**
     * 根据hash获取文件信息
     *
     * @param hash 文件hash
     * @return 文件信息
     */
    fun getFileInfoByHash(hash: String): FileInfo

    /**
     * 根据hash判断文件是否存在
     *
     * @param hash 文件hash
     * @return 文件是否存在
     */
    fun existByHash(hash: String): Boolean

    /**
     * 保存文件
     */
    fun saveFile(user: User, files: List<MultipartFile>): List<FileInfo>

    /**
     * 记录下载
     */
    fun recordDownload(fileInfoId: String)

    /**
     * 删除文件
     */
    fun deleteFile(id: String)

    /**
     * 获取所有文件信息
     */
    fun getAllFileInfo(): Stream<FileInfo>

    /**
     * 添加文件信息
     */
    fun addFileInfo(fileInfo: FileInfo)

    /**
     * 获取文件信息列表
     * @param pageIndex 页码
     * @param pageSize 每页数量
     * @param filter 过滤条件
     */
    fun find(pageIndex: Int, pageSize: Int, filter: FileInfoFilter?): Page<FileInfo>

    /**
     * 锁定文件
     */
    fun lock(fileInfoId: String, locked: Boolean)

    /**
     * 删除文件
     */
    fun delete(fileInfoId: String)
}
