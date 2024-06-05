package com.akagiyui.drive.controller

import com.akagiyui.drive.component.CurrentUser
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.response.sharing.SharingResponse
import com.akagiyui.drive.model.response.sharing.toResponse
import com.akagiyui.drive.service.SharingService
import org.springframework.web.bind.annotation.*

/**
 * 分享资源 控制器
 * @author AkagiYui
 */
@RestController
@RequestMapping("/share")
class SharingController(private val sharingService: SharingService) {

    /**
     * 获取分享列表
     *
     * @param user 用户
     * @return 分享列表
     */
    @GetMapping
    fun getSharingList(@CurrentUser user: User): List<SharingResponse> {
        return sharingService.list(user.id).toResponse()
    }

    /**
     * 创建分享
     *
     * @param id 文件ID
     * @param user 用户
     * @return 分享信息
     */
    @PostMapping("/{id}")
    fun createSharing(@PathVariable id: String, @CurrentUser user: User): SharingResponse {
        return SharingResponse(sharingService.createSharing(user, id))
    }

    /**
     * 删除分享
     *
     * @param id 分享ID
     * @param user 用户
     */
    @DeleteMapping("/{id}")
    fun deleteSharing(@PathVariable id: String, @CurrentUser user: User) {
        sharingService.deleteSharing(user.id, id)
    }

    /**
     * 获取分享
     *
     * @param id 分享ID
     * @param user 用户
     * @return 分享信息
     */
    @GetMapping("/{id}")
    fun getSharing(@PathVariable id: String, @CurrentUser user: User?): SharingResponse? {
        val sharing = sharingService.getSharing(id) ?: return null
        return SharingResponse(sharing)
    }

    /**
     * 根据用户文件ID获取分享
     *
     * @param id 文件ID
     * @param user 用户
     * @return 分享信息
     */
    @GetMapping("/file/{id}")
    fun getSharingByUserFileId(@PathVariable id: String, @CurrentUser user: User): SharingResponse? {
        val sharing = sharingService.getSharingByUserFile(id, user.id) ?: return null
        return SharingResponse(sharing)
    }
}
