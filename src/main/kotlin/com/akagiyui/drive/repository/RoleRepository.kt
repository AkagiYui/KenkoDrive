package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

/**
 * 角色表 操作接口
 *
 * @author AkagiYui
 */
interface RoleRepository : JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {
    /**
     * 找出所有默认角色
     *
     * @return 默认角色列表
     */
    fun findAllByIsDefaultIsTrue(): MutableSet<Role>

    /**
     * 角色名是否存在
     *
     * @param name 角色名
     * @return 是否存在
     */
    fun existsByName(name: String): Boolean

    /**
     * 根据角色id查找用户id
     *
     * @param id 角色id
     * @return 用户id列表
     */
    @Query(value = "SELECT user_id FROM user_role WHERE role_id = :roleId", nativeQuery = true)
    fun findUserIdsById(@Param("roleId") id: String): List<String>

}
