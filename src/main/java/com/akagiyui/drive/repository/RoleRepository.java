package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色表 操作接口
 *
 * @author AkagiYui
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    /**
     * 找出所有默认角色
     *
     * @return 默认角色列表
     */
    List<Role> findAllByIsDefaultIsTrue();

    /**
     * 角色名是否存在
     *
     * @param name 角色名
     * @return 是否存在
     */
    boolean existsByName(String name);
}
