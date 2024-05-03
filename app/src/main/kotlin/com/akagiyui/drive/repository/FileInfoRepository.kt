package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.FileInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Stream

/**
 * 文件信息操作接口
 *
 * @author AkagiYui
 */
interface FileInfoRepository : JpaRepository<FileInfo, String> {
    /**
     * 根据哈希查找文件
     *
     * @param hash 文件哈希
     * @return 文件
     */
    fun getFirstByHash(hash: String): FileInfo?

    /**
     * 根据哈希查找文件
     *
     * @param hash 文件哈希
     * @return 是否存在
     */
    fun existsByHash(hash: String): Boolean

    /**
     * 记录一次下载
     *
     * @param id 文件ID
     */
    @Modifying
    @Transactional
    @Query("update FileInfo f set f.downloadCount = f.downloadCount + 1 where f.id = ?1")
    fun recordDownload(id: String)

    /**
     * 获取所有文件信息，按更新时间升序，优先遍历长时间未更新的文件
     *
     * @return 文件信息流
     */
    fun findAllByOrderByUpdateTimeAsc(): Stream<FileInfo>
}
