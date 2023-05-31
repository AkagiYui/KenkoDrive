package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author AkagiYui
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户
     */
    User getFirstByUsername(String username);
}
