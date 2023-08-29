package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 文件信息操作接口
 *
 * @author AkagiYui
 */
@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, String> {
    /**
     * 根据哈希查找文件
     *
     * @param hash 文件哈希
     * @return 文件
     */
    FileInfo getFirstByHash(String hash);

    /**
     * 根据哈希查找文件
     *
     * @param hash 文件哈希
     * @return 是否存在
     */
    boolean existsByHash(String hash);

    @Query("update FileInfo f set f.downloadCount = f.downloadCount + 1 where f.id = ?1")
    void recordDownload(String id);
}
