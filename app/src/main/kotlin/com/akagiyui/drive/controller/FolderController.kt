package com.akagiyui.drive.controller

import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.component.CurrentUser
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.FolderContentFilter
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.folder.CreateFolderRequest
import com.akagiyui.drive.model.response.file.toResponse
import com.akagiyui.drive.model.response.folder.FolderContentResponse
import com.akagiyui.drive.model.response.folder.FolderResponse
import com.akagiyui.drive.model.response.folder.toResponse
import com.akagiyui.drive.service.FolderService
import com.akagiyui.drive.service.UserFileService
import org.springframework.web.bind.annotation.*

/**
 * 文件夹 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/folder")
class FolderController(private val folderService: FolderService, private val userFileService: UserFileService) {

    /**
     * 获取文件夹内容
     *
     * @param folderId 文件夹ID
     * @return 文件夹信息
     */
    @GetMapping("/{id}", "", "/")
    @RequirePermission
    fun getFolderContent(
        @PathVariable(name = "id", required = false) folderId: String?,
        @ModelAttribute filter: FolderContentFilter?,
        @CurrentUser user: User,
    ): FolderContentResponse {
        if (filter != null && filter.expression.hasText()) {
            return FolderContentResponse(
                userFileService.searchFiles(user.id, filter).toResponse(),
                emptyList(),
                listOf(FolderResponse("-1", "搜索结果", System.currentTimeMillis()))
            )
        }

        val files = userFileService.getFiles(user.id, folderId).toResponse()
        val folders = folderService.getSubFolders(user.id, folderId).toResponse()
        return FolderContentResponse(
            files,
            folders,
            folderId?.let { folderService.getFolderChain(user.id, it) } ?: emptyList())
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

    /**
     * 删除文件夹
     *
     * @param folderId 文件夹ID
     */
    @DeleteMapping("/{folderId}")
    @RequirePermission(Permission.FOLDER_DELETE)
    fun deleteFolder(@PathVariable folderId: String, @CurrentUser user: User) {
        folderService.deleteFolder(user.id, folderId)
    }

    /**
     * 移动文件夹
     *
     * @param folderId 文件夹ID
     * @param parentId 父文件夹ID
     */
    @PutMapping("/{folderId}/move")
    @RequirePermission
    fun moveFolder(
        @PathVariable folderId: String,
        @RequestParam("parent", required = false) parentId: String?,
        @CurrentUser user: User,
    ) {
        folderService.moveFolder(user.id, folderId, parentId)
    }

    /**
     * 重命名文件夹
     *
     * @param folderId 文件夹ID
     * @param name 新文件夹名
     * @param user 用户
     */
    @PutMapping("/{folderId}/name")
    @RequirePermission
    fun renameFolder(
        @PathVariable folderId: String,
        @RequestParam("name") name: String,
        @CurrentUser user: User,
    ) {
        folderService.rename(user.id, folderId, name)
    }
}
