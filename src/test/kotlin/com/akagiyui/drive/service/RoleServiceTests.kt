package com.akagiyui.drive.service

import com.akagiyui.drive.model.Permission
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * 角色服务测试类
 * @author AkagiYui
 */
@SpringBootTest
class RoleServiceTests @Autowired constructor(
    private val roleService: RoleService,
) {

    /**
     * 获取所有角色
     */
    @Test
    fun getAllPermissions() {
        val expected = listOf(*Permission.entries.toTypedArray())
        val actual = roleService.allPermissions
        Assertions.assertEquals(expected, actual)
    }

}
