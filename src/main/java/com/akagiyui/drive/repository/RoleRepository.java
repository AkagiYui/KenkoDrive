package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 角色表操作接口
 * @author AkagiYui
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
