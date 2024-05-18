package com.akagiyui.drive.task

import com.akagiyui.drive.entity.Role
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.AddUserRequest
import com.akagiyui.drive.service.RoleService
import com.akagiyui.drive.service.SettingService
import com.akagiyui.drive.service.UserService
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * 初始化任务，用于初始化一些数据
 * 注意：该任务具有危险性，也许会破坏数据，并且仅在第一次启动时自动执行
 *
 * @author AkagiYui
 */
@Component
class InitializeTasks(
    private val settingService: SettingService,
    private val roleService: RoleService,
    private val userService: UserService,
    private val minioClient: MinioClient,
    @Value("\${application.storage.minio.bucket}") private val bucketName: String,
) {

    fun preCheck() {
        log.info("Start pre check")
        // 检查是否已经初始化
        if (settingService.initialized) {
            log.warn("Already initialized")
        }
    }

    fun initConfig() {
        log.info("Start init config")
        // 初始化配置
        // 设置注册功能关闭
        settingService.registerEnabled = false
        // 设置文件上传大小限制为100MB
        settingService.fileUploadMaxSize = 1024L * 1024 * 100
        // 设置文件上传分块大小为5MB
        settingService.fileUploadChunkSize = 1024 * 1024 * 5
    }

    fun addRoleAndUser() {
        log.info("Start add role")
        // 添加角色
        // 添加超级管理员角色
        val admin = Role().apply {
            name = "超级管理员"
            description = "拥有所有权限"
            disabled = false
            isDefault = false
            permissions = Permission.entries.toMutableSet()
        }
        roleService.addRole(admin)

        // 添加管理员用户
        val adminUserRequest = AddUserRequest().apply {
            username = "admin"
            password = "admin"
            nickname = "管理员"
        }
        val adminUser = userService.addUser(adminUserRequest)
        userService.addRoles(adminUser.id, HashSet(listOf(admin.id)))

        // 添加普通用户角色
        val user = Role().apply {
            name = "普通用户"
            description = "允许上传下载自己的文件"
            disabled = false
            isDefault = true
            permissions = mutableSetOf(
                Permission.PERSONAL_UPLOAD,
                Permission.PERSONAL_DOWNLOAD,
                Permission.FOLDER_CREATE,
                Permission.FOLDER_DELETE,
            )
        }
        roleService.addRole(user)
    }

    fun initMinio() {
        log.info("Start init minio")
        // 初始化 Minio
        // 创建默认存储桶
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            try {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
            } catch (e: Exception) {
                log.error("Create bucket failed", e)
            }
        }
    }
}
