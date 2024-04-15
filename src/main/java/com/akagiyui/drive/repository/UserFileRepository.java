package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.Folder;
import com.akagiyui.drive.entity.UserFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户文件关联 操作接口
 *
 * @author AkagiYui
 */
@Repository
public interface UserFileRepository extends JpaRepository<UserFile, String> {

    /**
     * 根据用户ID、文件信息ID、文件夹判断用户文件是否存在
     *
     * @param userId     用户ID
     * @param fileInfoId 文件信息ID
     * @param folder     文件夹
     * @return 用户文件是否存在
     */
    boolean existsByUserIdAndFileInfoIdAndFolder(String userId, String fileInfoId, Folder folder);

    /**
     * 根据用户ID、文件夹ID获取用户文件列表
     *
     * @param userId   用户ID
     * @param folderId 文件夹ID
     * @return 用户文件列表
     */
    List<UserFile> findByUserIdAndFolderId(String userId, String folderId);

    /**
     * 根据文件信息ID判断用户文件是否存在
     *
     * @param fileInfoId 文件信息ID
     * @return 用户文件是否存在
     */
    boolean existsByFileInfoId(String fileInfoId);

    /**
     * 根据用户ID、ID获取用户文件
     *
     * @param userId 用户ID
     * @param id     ID
     * @return 用户文件
     */
    UserFile findByUserIdAndId(String userId, String id);
}
