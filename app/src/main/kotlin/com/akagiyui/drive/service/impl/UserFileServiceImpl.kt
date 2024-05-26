package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.model.request.MirrorFileRequest
import com.akagiyui.drive.repository.UserFileRepository
import com.akagiyui.drive.service.FileInfoService
import com.akagiyui.drive.service.FolderService
import com.akagiyui.drive.service.UserFileService
import jakarta.annotation.Resource
import org.springframework.context.annotation.Lazy
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
    private val folderService: FolderService,
    private val redisCache: RedisCache,
) : UserFileService {

    @Lazy
    @Resource
    private lateinit var fileInfoService: FileInfoService

    override fun addAssociation(user: User, userFileName: String, fileInfo: FileInfo, folderId: String?): UserFile {
        val folder = if (folderId.hasText()) folderService.getFolderById(folderId) else null
        var fileName = userFileName
        while (userFileRepository.existsByUserIdAndNameAndFolder(user.id, fileName, folder)) {
            val index = fileName.lastIndexOf(".")
            val name = fileName.substring(0, index)
            val suffix = fileName.substring(index)
            fileName = "$name(${UUID.randomUUID()})$suffix"
        }
        val userFile = UserFile().apply {
            this.user = user
            this.fileInfo = fileInfo
            name = fileName
            this.folder = folder
        }
        return userFileRepository.save(userFile)
    }

    override fun getFiles(userId: String, folderId: String?): List<UserFile> {
        return userFileRepository.findByUserIdAndFolderId(userId, folderId)
    }

    override fun existByFileId(fileId: String): Boolean {
        return userFileRepository.existsByFileInfoId(fileId)
    }

    override fun getUserFileById(userId: String, id: String): UserFile {
        return userFileRepository.findByUserIdAndId(userId, id) ?: throw CustomException(ResponseEnum.NOT_FOUND)
    }

    override fun createTemporaryId(userId: String, userFileId: String): Pair<String, UserFile> {
        val userFile = getUserFileById(userId, userFileId)
        if (userFile.fileInfo.locked) {
            throw CustomException(ResponseEnum.FILE_LOCKED)
        }
        val randomId = UUID.randomUUID().toString().replace("-", "")
        val redisKey = "download:$randomId"
        redisCache[redisKey, 1, TimeUnit.HOURS] = userFile.id
        return randomId to userFile
    }

    override fun getFileInfoByTemporaryId(temporaryId: String): UserFile {
        val redisKey = "download:$temporaryId"
        val userFileId: String = redisCache[redisKey] ?: throw CustomException(ResponseEnum.NOT_FOUND)
        return userFileRepository.findById(userFileId).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
    }

    override fun userDeleteFile(userId: String, id: String) {
        val userFile = getUserFileById(userId, id)
        userFileRepository.delete(userFile)
    }

    override fun mirrorFile(user: User, request: MirrorFileRequest): UserFile {
        val fileInfo = fileInfoService.getFileInfoByHash(request.hash)
        val folder = if (request.folder.hasText()) {
            folderService.getFolderById(request.folder!!)
        } else {
            null
        }
        return addAssociation(user, request.filename, fileInfo, folder?.id)
    }

    override fun moveFile(userId: String, fileId: String, folderId: String?) {
        val userFile = getUserFileById(userId, fileId)
        userFile.folder = if (folderId.hasText()) {
            val folder = folderService.getFolderById(folderId)
            if (folder.user.id != userId) {
                throw CustomException(ResponseEnum.NOT_FOUND)
            }
            folder
        } else {
            null
        }
        userFileRepository.save(userFile)
    }
}
