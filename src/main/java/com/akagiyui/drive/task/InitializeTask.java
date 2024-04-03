package com.akagiyui.drive.task;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.service.ConfigService;
import com.akagiyui.drive.service.RoleService;
import com.akagiyui.drive.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * 初始化任务，用于初始化一些数据
 * 注意：该任务具有危险性，也许会破坏数据，并且仅在第一次启动时自动执行
 *
 * @author AkagiYui
 */
@Component
@Slf4j
public class InitializeTask {

    private final ConfigService configService;
    private final RoleService roleService;
    private final UserService userService;

    public InitializeTask(@Autowired ConfigService configService,
                          @Autowired RoleService roleService,
                        @Autowired UserService userService) {
        this.configService = configService;
        this.roleService = roleService;
        this.userService = userService;
    }

    public void preCheck() {
        log.info("Start pre check");
        // 检查是否已经初始化
        if (configService.isInitialized()) {
            log.warn("Already initialized");
        }
    }

    public void initConfig() {
        log.info("Start init config");
        // 初始化配置
        // 设置注册功能关闭
        configService.setRegisterEnabled(false);
        // 设置文件上传大小限制为100MB
        configService.setFileUploadMaxSize(1024L * 1024 * 100);
        // 设置文件上传分块大小为5MB
        configService.setFileUploadChunkSize(1024 * 1024 * 5);
    }

    public void addRoleAndUser() {
        log.info("Start add role");
        // 添加角色
        // 添加超级管理员角色
        Role admin = new Role();
        admin.setName("超级管理员");
        admin.setDescription("拥有所有权限");
        admin.setDisabled(false);
        admin.setIsDefault(false);
        admin.setPermissions(new HashSet<>(Arrays.asList(Permission.values())));
        roleService.addRole(admin);

        // 添加管理员用户
        AddUserRequest adminUserRequest = new AddUserRequest();
        adminUserRequest.setUsername("admin");
        adminUserRequest.setPassword("admin");
        adminUserRequest.setNickname("管理员");
        User adminUser = userService.addUser(adminUserRequest);
        userService.addRoles(adminUser.getId(), new HashSet<>(List.of(admin.getId())));

        // 添加普通用户角色
        Role user = new Role();
        user.setName("普通用户");
        user.setDescription("允许上传下载自己的文件");
        user.setDisabled(false);
        user.setIsDefault(true);
        user.setPermissions(new HashSet<>(Arrays.asList(
            Permission.PERSONAL_UPLOAD,
            Permission.PERSONAL_DOWNLOAD
        )));
        roleService.addRole(user);
    }
}
