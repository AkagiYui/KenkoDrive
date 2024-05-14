package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.entity.Folder
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.response.FolderResponse
import com.akagiyui.drive.repository.FolderRepository
import com.akagiyui.drive.service.FolderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * 文件夹服务实现类
 *
 * @author AkagiYui
 */
@Service
class FolderServiceImpl @Autowired constructor(
    private val folderRepository: FolderRepository,
) : FolderService {

    override fun createFolder(user: User, name: String, parentId: String?): Folder {
        val resolvedParentId = if (parentId.hasText()) parentId else null
        val parentFolder = resolvedParentId?.let {
            folderRepository.findById(it).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
        }

        if (folderRepository.existsByNameAndUserIdAndParentId(name, user.id, resolvedParentId)) {
            throw CustomException(ResponseEnum.FOLDER_EXIST)
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
        val resolvedParentId = if (parentId.hasText()) parentId else null

        return folderRepository.findByUserIdAndParentId(userId, resolvedParentId)
    }

    override fun getFolderChain(userId: String, folderId: String): List<FolderResponse> {
        if (!folderId.hasText()) {
            return listOf()
        }

        val folder = folderRepository.findById(folderId).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
        if (folder.user.id != userId) {
            throw CustomException(ResponseEnum.NOT_FOUND)
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
        return folderRepository.findById(folderId).orElseThrow { CustomException(ResponseEnum.NOT_FOUND) }
    }
}
