package com.akagiyui.drive.service.impl

import cn.hutool.crypto.digest.DigestAlgorithm
import cn.hutool.crypto.digest.Digester
import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.BusinessException
import com.akagiyui.common.utils.hasText
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.FileInfo_
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.CacheConstants
import com.akagiyui.drive.model.FileInfoFilter
import com.akagiyui.drive.repository.FileInfoRepository
import com.akagiyui.drive.service.FileInfoService
import com.akagiyui.drive.service.StorageService
import com.akagiyui.drive.service.UserFileService
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*
import java.util.stream.Stream

/**
 * 文件信息接口实现类
 *
 * @author AkagiYui
 */
@Service
class FileInfoServiceImpl(
    private val fileInfoRepository: FileInfoRepository,
    private val storageService: StorageService,
    private val userFileService: UserFileService,
) : FileInfoService {

    private val log by LoggerDelegate()

    @Cacheable(value = [CacheConstants.FILE_INFO], key = "#id")
    override fun getFileInfo(id: String): FileInfo {
        return fileInfoRepository.findById(id).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
    }

    override fun getFileInfoByHash(hash: String): FileInfo {
        return fileInfoRepository.getFirstByHash(hash) ?: throw BusinessException(ResponseEnum.NOT_FOUND)
    }

    override fun existByHash(hash: String): Boolean {
        return fileInfoRepository.existsByHash(hash)
    }

    private fun getFileInfoWithoutCache(id: String): FileInfo {
        return fileInfoRepository.findById(id).orElseThrow { BusinessException(ResponseEnum.NOT_FOUND) }
    }

    override fun saveFile(user: User, files: List<MultipartFile>): List<FileInfo> {
        val fileInfos = mutableListOf<FileInfo>()
        files.forEach { file ->
            // 读取文件信息
            val filename = file.originalFilename ?: UUID.randomUUID().toString()
            val fileSize = file.size
            val contentType = file.contentType ?: ""
            val fileBytes = try {
                file.inputStream.readAllBytes()
            } catch (e: IOException) {
                throw BusinessException(ResponseEnum.INTERNAL_ERROR)
            }

            // 计算文件md5
            val digester = Digester(DigestAlgorithm.MD5)
            val hash = digester.digestHex(fileBytes)

            // 文件未存在
            if (!fileInfoRepository.existsByHash(hash)) {
                // 新增文件记录
                val fileInfo = FileInfo().apply {
                    name = filename
                    size = fileSize
                    type = contentType
                    this.hash = hash
                    storageKey = hash
                    downloadCount = 0L
                }
                fileInfoRepository.save(fileInfo)
                // 保存二进制内容
                storageService.store(fileInfo.storageKey, fileBytes, null)
            } else {
                log.debug("文件已存在，hash: {}", hash)
            }
            // 添加用户与文件的关联记录
            fileInfoRepository.getFirstByHash(hash)?.let { fileInfo ->
                userFileService.addAssociation(user, filename, fileInfo, null)
                fileInfos.add(fileInfo) // 记录返回结果
            } ?: throw BusinessException(ResponseEnum.INTERNAL_ERROR)
        }
        return fileInfos
    }

    @CacheEvict(value = [CacheConstants.FILE_INFO], key = "#fileInfoId")
    override fun recordDownload(fileInfoId: String) {
        fileInfoRepository.recordDownload(fileInfoId)
    }

    @CacheEvict(value = [CacheConstants.FILE_INFO], key = "#id")
    override fun deleteFile(id: String) {
        val fileInfo = getFileInfoWithoutCache(id)
        storageService.delete(fileInfo.storageKey)
        fileInfoRepository.delete(fileInfo)
    }

    override fun getAllFileInfo(): Stream<FileInfo> {
        return fileInfoRepository.findAllByOrderByUpdateTimeAsc()
    }

    override fun addFileInfo(fileInfo: FileInfo) {
        fileInfoRepository.save(fileInfo)
    }

    override fun find(pageIndex: Int, pageSize: Int, filter: FileInfoFilter?): Page<FileInfo> {
        val pageable = PageRequest.of(pageIndex, pageSize)

        // 条件查询
        val specification = Specification { root, _, cb ->
            filter?.expression.hasText {
                val likePattern = "%$it%"
                val namePredicate = cb.like(root[FileInfo_.name], likePattern)
                val typePredicate = cb.like(root[FileInfo_.type], likePattern)
                cb.or(namePredicate, typePredicate)
            }
        }

        return fileInfoRepository.findAll(specification, pageable)
    }

    override fun lock(fileInfoId: String, locked: Boolean) {
        val fileInfo = getFileInfoWithoutCache(fileInfoId)
        fileInfo.locked = locked
        fileInfoRepository.save(fileInfo)
    }

    override fun delete(fileInfoId: String) {
        val fileInfo = getFileInfoWithoutCache(fileInfoId)
        // 删除用户的文件关联
        userFileService.removeAllAssociation(fileInfo.id)
        // 删除文件本体
        storageService.delete(fileInfo.storageKey)
        // 删除文件记录
        fileInfoRepository.delete(fileInfo)
    }
}
