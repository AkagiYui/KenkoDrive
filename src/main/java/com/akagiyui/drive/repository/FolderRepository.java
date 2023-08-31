package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 文件夹表 操作接口
 *
 * @author AkagiYui
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, String> {
    /**
     * 根据父文件夹ID获取子文件夹列表
     *
     * @param parentId 父文件夹ID
     * @return 子文件夹列表
     */
    List<Folder> findByUserIdAndParentId(String userId, String parentId);

    /**
     * 根据文件夹名、用户ID、父文件夹ID判断文件夹是否存在
     *
     * @param name 文件夹名
     * @param userId 用户ID
     * @param parentId 父文件夹ID
     * @return 文件夹是否存在
     */
    boolean existsByNameAndUserIdAndParentId(String name, String userId, String parentId);
}
