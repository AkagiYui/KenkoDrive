package com.akagiyui.drive.service;

import com.akagiyui.drive.model.Permission;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 角色服务测试类
 * @author AkagiYui
 */
@SpringBootTest
class RoleServiceTest {

    @Resource
    private RoleService roleService;

    /**
     * 获取所有角色
     */
    @Test
    void getAllPermissions() {
        List<Permission> expected = List.of(Permission.values());
        List<Permission> actual = roleService.getAllPermissions();
        assertEquals(expected, actual);
    }
}
