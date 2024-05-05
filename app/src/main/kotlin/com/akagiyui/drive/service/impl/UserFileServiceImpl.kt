package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.repository.UserFileRepository
import com.akagiyui.drive.service.FolderService
import com.akagiyui.drive.service.UserFileService
import com.akagiyui.drive.service.UserService
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * 用户文件关联 服务实现类
 *
 * @author AkagiYui
 */
@Service
class UserFileServiceImpl(
    private val userFileRepository: UserFileRepository,
    private val userService: UserService,
    private val folderService: FolderService,
    private val redisCache: RedisCache,
) : UserFileService {

    override fun addAssociation(user: User, fileInfo: FileInfo, folderId: String?): UserFile {
        val folder = if (folderId.hasText()) folderService.getFolderById(folderId!!) else null
        if (userFileRepository.existsByUserIdAndFileInfoIdAndFolder(user.id, fileInfo.id, folder)) {
            throw RuntimeException("文件已存在")
        }
        val userFile = UserFile().apply {
            this.user = user
            this.fileInfo = fileInfo
            name = fileInfo.name
            this.folder = folder
        }
        return userFileRepository.save(userFile)
    }

    override fun getFiles(folderId: String?): List<UserFile> {
        val user = userService.getUser()
        return userFileRepository.findByUserIdAndFolderId(user.id, folderId)
    }

    override fun existByFileId(fileId: String): Boolean {
        return userFileRepository.existsByFileInfoId(fileId)
    }

    override fun getUserFileById(id: String): UserFile {
        val user = userService.getUser()
        return userFileRepository.findByUserIdAndId(user.id, id) ?: throw CustomException(ResponseEnum.NOT_FOUND)
    }

    override fun getTemporaryId(userFileId: String): String {
        val userFile = getUserFileById(userFileId)
        val randomId = UUID.randomUUID().toString().replace("-", "")
        val redisKey = "download:$randomId"
        redisCache[redisKey, 1, TimeUnit.HOURS] = userFile.id
        return randomId
    }

    override fun getFileInfoByTemporaryId(temporaryId: String): UserFile {
        val redisKey = "download:$temporaryId"
        val userFileId: String = redisCache[redisKey] ?: throw CustomException(ResponseEnum.NOT_FOUND)
        return userFileRepository.findById(userFileId).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
    }
}
