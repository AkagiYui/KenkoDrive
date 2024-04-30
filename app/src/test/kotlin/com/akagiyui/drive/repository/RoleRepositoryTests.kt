package com.akagiyui.drive.repository

import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.model.Permission
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * 角色表操作接口测试类
 *
 * @author AkagiYui
 */
@SpringBootTest
class RoleRepositoryTests @Autowired constructor(
    private val roleRepository: RoleRepository,
) {

    @Test
    fun findAllTest() {
        Assertions.assertNotNull(roleRepository.findAll())
    }

    @Test
    @Transactional
    fun addNormalUserRole() {
        val role = Role().apply {
            name = "普通用户1"
            description = "默认角色，允许文件上传、文件下载。"
            permissions = mutableSetOf(Permission.PERSONAL_DOWNLOAD, Permission.PERSONAL_UPLOAD)
        }
        roleRepository.save(role)
        Assertions.assertNotNull(role.id)
    }

    @Test
    @Transactional
    fun addUserAdminRole() {
        val role = Role().apply {
            name = "用户管理员"
            description = "允许用户查询、用户增删、用户修改。"
            permissions = mutableSetOf(
                Permission.USER_VIEW,
                Permission.USER_ADD,
                Permission.USER_UPDATE,
                Permission.USER_DELETE
            )
        }
        roleRepository.save(role)
        Assertions.assertNotNull(role.id)
    }

    @Test
    @Transactional
    fun addAnnouncementAdminRole() {
        val role = Role().apply {
            name = "公告管理员"
            description = "允许获取所有公告、公告增删、公告修改。"
            permissions = mutableSetOf(
                Permission.ANNOUNCEMENT_GET_ALL,
                Permission.ANNOUNCEMENT_ADD,
                Permission.ANNOUNCEMENT_UPDATE,
                Permission.ANNOUNCEMENT_DELETE
            )
        }
        roleRepository.save(role)
        Assertions.assertNotNull(role.id)
    }

}
