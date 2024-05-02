package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.repository.UserFileRepository
import com.akagiyui.drive.service.FolderService
import com.akagiyui.drive.service.UserFileService
import com.akagiyui.drive.service.UserService
import org.springframework.stereotype.Service

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
) : UserFileService {

    override fun addAssociation(user: User, fileInfo: FileInfo, folderId: String?) {
        val folder = if (folderId.hasText()) folderService.getFolderById(folderId!!) else null
        if (userFileRepository.existsByUserIdAndFileInfoIdAndFolder(user.id, fileInfo.id, folder)) {
            return
        }

        val userFile = UserFile().apply {
            this.user = user
            this.fileInfo = fileInfo
            name = fileInfo.name
            this.folder = folder
        }
        userFileRepository.save(userFile)
    }

    override fun getFiles(folderId: String?): List<UserFile> {
        val user = userService.getUser()
        return userFileRepository.findByUserIdAndFolderId(user.id, folderId)
    }

    override fun existByFileId(fileId: String): Boolean {
        return userFileRepository.existsByFileInfoId(fileId)
    }

    override fun getFileInfo(id: String): FileInfo {
        val user = userService.getUser()
        val association = userFileRepository.findByUserIdAndId(user.id, id)
            ?: throw CustomException(ResponseEnum.NOT_FOUND)
        return association.fileInfo
    }
}
