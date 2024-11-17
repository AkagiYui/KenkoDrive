package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.BusinessException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.entity.Folder
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.response.folder.FolderResponse
import com.akagiyui.drive.repository.FolderRepository
import com.akagiyui.drive.service.FolderService
import com.akagiyui.drive.service.UserFileService
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 文件夹服务实现类
 *
 * @author AkagiYui
 */
@Service
class FolderServiceImpl @Autowired constructor(
    private val folderRepository: FolderRepository,
) : FolderService {
    private val log by LoggerDelegate()

    @Lazy
    @Resource
    private lateinit var userFileService: UserFileService

    override fun createFolder(user: User, name: String, parentId: String?): Folder {
        val resolvedParentId = parentId?.takeIf { it.hasText() }
        val parentFolder = resolvedParentId?.let {
            folderRepository.findById(it).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
        }

        if (folderRepository.existsByNameAndUserIdAndParentId(name, user.id, resolvedParentId)) {
            throw BusinessException(ResponseEnum.FOLDER_EXIST)
        }

        val folder = Folder().apply {
            this.name = name
            this.user = user
            this.parent = parentFolder
        }
        folderRepository.save(folder)
        return folder
    }

    override fun getSubFolders(userId: String, parentId: String?): List<Folder> {
        val resolvedParentId = parentId?.takeIf { it.hasText() }
        return folderRepository.findByUserIdAndParentId(userId, resolvedParentId)
    }

    override fun getFolderChain(userId: String, folderId: String): List<FolderResponse> {
        if (!folderId.hasText()) {
            return listOf()
        }

        val folder = folderRepository.findById(folderId).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
        if (folder.user.id != userId) {
            throw BusinessException(ResponseEnum.NOT_FOUND)
        }

        val folderChain = mutableListOf<FolderResponse>()
        var currentFolder: Folder? = folder
        while (currentFolder != null) {
            folderChain.add(0, FolderResponse(currentFolder))
            currentFolder = currentFolder.parent
        }

        return folderChain.toList()
    }

    override fun getFolderById(folderId: String): Folder {
        return folderRepository.findById(folderId).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
    }

    @Transactional
    override fun deleteFolder(userId: String, folderId: String) {
        val folder = folderRepository.findById(folderId).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
        if (folder.user.id != userId) {
            throw BusinessException(ResponseEnum.NOT_FOUND)
        }

        // 删除文件夹下的文件和子文件夹
        folder.files.forEach {
            userFileService.userDeleteFile(userId, it.id)
        }
        folder.subFolders.forEach {
            deleteFolder(userId, it.id)
        }

        folderRepository.delete(folder)
        log.debug("delete folder: $folderId")
    }

    override fun rename(userId: String, folderId: String, newName: String) {
        val folder = folderRepository.findByUserIdAndId(userId, folderId)
            ?: throw BusinessException(ResponseEnum.NOT_FOUND)
        folder.name = newName
        folderRepository.save(folder)
    }

    override fun moveFolder(userId: String, folderId: String, parentId: String?) {
        val folder = folderRepository.findById(folderId).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
        if (folder.user.id != userId) {
            throw BusinessException(ResponseEnum.NOT_FOUND)
        }

        check(parentId != folderId) { "not allowed to move folder to itself" }
        val parentFolder = parentId.hasText {
            folderRepository.findById(it).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
        }

        folder.parent = parentFolder
        folderRepository.save(folder)
    }
}
