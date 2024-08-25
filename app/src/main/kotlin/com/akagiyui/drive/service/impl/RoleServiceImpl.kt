package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.entity.Role_
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.CacheConstants
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.RoleFilter
import com.akagiyui.drive.model.request.role.AddRoleRequest
import com.akagiyui.drive.model.request.role.UpdateRoleRequest
import com.akagiyui.drive.repository.RoleRepository
import com.akagiyui.drive.service.RoleService
import org.hibernate.Hibernate
import org.springframework.cache.annotation.CacheEvict
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 角色服务实现类
 *
 * @author AkagiYui
 */
@Service
class RoleServiceImpl(private val roleRepository: RoleRepository) : RoleService {
    private val log by LoggerDelegate()

    /**
     * 根据ID查找角色，不存在则抛出异常
     *
     * @param id 角色ID
     * @return 角色
     */
    private fun getRoleById(id: String): Role {
        return roleRepository.findById(id).orElseThrow {
            log.warn("角色不存在: {}", id)
            CustomException(ResponseEnum.ROLE_NOT_EXIST)
        }
    }

    override fun getAllRoles(): List<Role> {
        return roleRepository.findAll()
    }

    override fun getAllDefaultRoles(): MutableSet<Role> {
        return roleRepository.findAllByIsDefaultIsTrue()
    }

    override fun find(index: Int, size: Int, filter: RoleFilter?): Page<Role> {
        val pageable = PageRequest.of(index, size)

        // 条件查询
        val specification = Specification { root, _, cb ->
            filter?.expression.hasText {
                val likePattern = "%$it%"
                val namePredicate = cb.like(root[Role_.name], likePattern)
                val descriptionPredicate = cb.like(root[Role_.description], likePattern)
                cb.or(namePredicate, descriptionPredicate)
            }
        }

        return roleRepository.findAll(specification, pageable)
    }

    override fun find(ids: Set<String>): Set<Role> {
        return HashSet(roleRepository.findAllById(ids))
    }

    override fun getAllPermissions(): List<Permission> {
        return Permission.entries
    }

    override fun addRole(role: AddRoleRequest): String {
        // 检查角色名是否重复
        if (roleRepository.existsByName(role.name)) {
            throw CustomException(ResponseEnum.ROLE_EXIST)
        }
        val permissions = try {
            // 检查权限是否存在
            role.permissions.map { Permission.valueOf(it) }.toMutableSet()
        } catch (e: IllegalArgumentException) {
            throw CustomException(ResponseEnum.PERMISSION_NOT_EXIST)
        }
        val newRole = Role().apply {
            name = role.name
            description = role.description
            this.permissions = permissions
            isDefault = role.default == true
        }
        roleRepository.save(newRole)
        return newRole.id
    }

    override fun addRole(role: Role): Role {
        return roleRepository.save(role)
    }

    override fun deleteRole(id: String) {
        val role = getRoleById(id)
        roleRepository.delete(role)
    }

    @CacheEvict(cacheNames = [CacheConstants.USER_BY_ID], allEntries = true)
    override fun updateRole(id: String, role: UpdateRoleRequest) {
        val oldRole = getRoleById(id)
        // 修改角色名
        if (role.name.hasText() && oldRole.name != role.name) {
            // 检查角色名是否重复
            if (roleRepository.existsByName(role.name!!)) {
                throw CustomException(ResponseEnum.ROLE_EXIST)
            }
            oldRole.name = role.name!!
        }
        // 修改角色描述
        role.description.hasText { oldRole.description = it }
        // 修改是否默认角色
        role.isDefault?.let { oldRole.isDefault = it }
        // 修改权限
        role.permissions?.let {
            val permissionsSet = try {
                it.map { perm -> Permission.valueOf(perm) }.toMutableSet()
            } catch (e: IllegalArgumentException) {
                log.warn("Permission not found: {}", it)
                throw CustomException(ResponseEnum.PERMISSION_NOT_EXIST)
            }
            oldRole.permissions = permissionsSet
        }
        roleRepository.save(oldRole)
    }

    @CacheEvict(cacheNames = [CacheConstants.USER_BY_ID], allEntries = true)
    override fun disable(id: String, disabled: Boolean) {
        val role = getRoleById(id)
        if (role.disabled != disabled) {
            role.disabled = disabled
            roleRepository.save(role)
        }
    }

    @Transactional
    override fun getUsers(id: String): Set<User> {
        val role = getRoleById(id)
        Hibernate.initialize(role.users) // 初始化LAZY字段，强制加载用户
        return role.users
    }

    override fun findUserIdsById(id: String): List<String> {
        return roleRepository.findUserIdsById(id)
    }
}
