package com.akagiyui.drive.controller

import com.akagiyui.drive.component.CurrentUser
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.CreateFolderRequest
import com.akagiyui.drive.model.response.FolderResponse
import com.akagiyui.drive.service.FolderService
import org.springframework.web.bind.annotation.*

/**
 * 文件夹 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/folder")
class FolderController(private val folderService: FolderService) {
    /**
     * 获取文件夹列表
     *
     * @param parentId 父文件夹ID
     * @return 文件夹列表
     */
    @GetMapping("", "/")
    @RequirePermission
    fun listFolder(
        @RequestParam(name = "parent", required = false) parentId: String?,
        @CurrentUser user: User,
    ): List<FolderResponse> {
        return FolderResponse.fromFolderList(folderService.getSubFolders(user.id, parentId))
    }

    /**
     * 创建文件夹
     *
     * @param request 创建文件夹请求
     * @return 文件夹信息
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.FOLDER_CREATE)
    fun createFolder(@RequestBody request: CreateFolderRequest, @CurrentUser user: User): FolderResponse {
        return FolderResponse(folderService.createFolder(user, request.name, request.parent))
    }
}
