package com.akagiyui.drive.service.impl

import com.akagiyui.common.utils.BASE_LOWER_CASE
import com.akagiyui.common.utils.BASE_NUMBER
import com.akagiyui.common.utils.random
import com.akagiyui.drive.entity.Sharing
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.repository.SharingRepository
import com.akagiyui.drive.service.SharingService
import com.akagiyui.drive.service.UserFileService
import org.springframework.stereotype.Service

/**
 * 分享资源 服务实现
 * @author AkagiYui
 */
@Service
class SharingServiceImpl(
    private val repository: SharingRepository,
    private val userFileService: UserFileService,
) : SharingService {
    override fun createSharing(user: User, userFileId: String): Sharing {
        val password = String.random("${String.BASE_LOWER_CASE}${String.BASE_NUMBER}", 4)
        val sharing = Sharing().apply {
            this.user = user
            this.file = userFileService.getUserFileById(user.id, userFileId)
            this.password = password
        }
        return repository.save(sharing)
    }

    override fun getSharingByUserFile(userFileId: String, userId: String): Sharing? {
        return repository.findByUserIdAndFileId(userId, userFileId)
    }

    override fun deleteSharing(userId: String, sharingId: String) {
        val sharing = repository.findByUserIdAndId(userId, sharingId) ?: throw RuntimeException("分享不存在")
        repository.delete(sharing)
    }

    override fun list(userId: String): List<Sharing> {
        return repository.findByUserId(userId)
    }

    override fun getSharing(sharingId: String): Sharing? {
        return repository.findById(sharingId).orElse(null)
    }
}
