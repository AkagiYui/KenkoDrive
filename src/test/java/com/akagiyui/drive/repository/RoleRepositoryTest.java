package com.akagiyui.drive.repository;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.model.Permission;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

/**
 * 角色表操作接口测试类
 *
 * @author AkagiYui
 */
@SpringBootTest
class RoleRepositoryTest {

    @Resource
    private RoleRepository roleRepository;

    @Test
    void findAllTest() {
        roleRepository.findAll().forEach(System.out::println);
    }

    @Test
    void addNormalUserRole() {
        Role role = new Role();
        role.setName("普通用户");
        role.setDescription("默认角色，允许文件上传、文件下载。");
        role.setPermissions(Set.of(Permission.PERSONAL_DOWNLOAD, Permission.PERSONAL_UPLOAD));
        roleRepository.save(role);
    }

    @Test
    void addUserAdminRole() {
        Role role = new Role();
        role.setName("用户管理员");
        role.setDescription("允许用户查询、用户增删、用户修改。");
        role.setPermissions(Set.of(Permission.USER_VIEW, Permission.USER_ADD, Permission.USER_UPDATE, Permission.USER_DELETE));
        roleRepository.save(role);
    }

    @Test
    void addAnnouncementAdminRole() {
        Role role = new Role();
        role.setName("公告管理员");
        role.setDescription("允许获取所有公告、公告增删、公告修改。");
        role.setPermissions(Set.of(
                Permission.ANNOUNCEMENT_GET_ALL,
                Permission.ANNOUNCEMENT_ADD,
                Permission.ANNOUNCEMENT_UPDATE,
                Permission.ANNOUNCEMENT_DELETE
        ));
        roleRepository.save(role);
    }
}
