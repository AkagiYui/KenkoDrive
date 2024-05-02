package com.akagiyui.drive.service.impl

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.utils.FileUtil
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.model.AvatarContent
import com.akagiyui.drive.service.AvatarService
import com.akagiyui.drive.service.StorageService
import com.akagiyui.drive.service.UserService
import net.coobird.thumbnailator.Thumbnails
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.imageio.ImageIO

/**
 * 头像服务实现类
 *
 * @author AkagiYui
 */
@Service
class AvatarServiceImpl(
    private val userService: UserService,
    private val storageService: StorageService,
) : AvatarService {
    private val log by LoggerDelegate()

    companion object {
        /**
         * 允许上传的头像的文件类型
         */
        private val AVATAR_TYPES = listOf(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/bmp",
            "image/gif",
            "image/pjpeg",
            "image/x-png",
        )

        /**
         * 头像文件大小的上限值(3MB)
         */
        const val AVATAR_MAX_SIZE: Int = 3 * 1024 * 1024

        /**
         * 高度限制
         */
        const val HEIGHT_LIMIT: Int = 200

        /**
         * 宽度限制
         */
        const val WIDTH_LIMIT: Int = 200

        /**
         * 头像图片格式
         */
        const val IMAGE_FORMAT: String = "jpg"
    }

    @Value("\${application.avatar.default}")
    private val defaultAvatarPath: String = "static/default-avatar.jpg"

    /**
     * 默认头像缓存，避免每次都读取文件
     */
    private val defaultAvatar: ByteArray by lazy {
        val file = FileUtil.getResourceFile(defaultAvatarPath)
        try {
            file.toURI().toURL().openStream().use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: IOException) {
            log.error("Load default avatar failed", e)
            throw CustomException(ResponseEnum.INTERNAL_ERROR)
        }
    }

    override fun getAvatar(): AvatarContent {
        val avatarKey = getAvatarKey()
        val avatar: ByteArray = if (storageService.exists(avatarKey)) {
            val file = storageService.get(avatarKey)
            try {
                file.inputStream.use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: IOException) {
                log.error("Load avatar failed", e)
                throw CustomException(ResponseEnum.INTERNAL_ERROR)
            }
        } else {
            defaultAvatar
        }
        return AvatarContent(avatar, IMAGE_FORMAT)
    }

    override fun saveAvatar(avatar: MultipartFile) {
        if (avatar.isEmpty) {
            throw CustomException(ResponseEnum.BAD_REQUEST)
        }
        if (avatar.size > AVATAR_MAX_SIZE) {
            throw CustomException(ResponseEnum.FILE_TOO_LARGE)
        }
        if (!AVATAR_TYPES.contains(avatar.contentType)) {
            throw CustomException(ResponseEnum.FILE_FORMAT_NOT_SUPPORT)
        }

        val image = try {
            ImageIO.read(avatar.inputStream)
        } catch (e: IOException) {
            log.error("Read avatar failed", e)
            throw CustomException(ResponseEnum.INTERNAL_ERROR)
        }

        val stream = ByteArrayOutputStream()
        Thumbnails.of(image)
            .size(WIDTH_LIMIT, HEIGHT_LIMIT)
            .outputFormat(IMAGE_FORMAT)
            .toOutputStream(stream)
        val content = stream.toByteArray()
        storageService.store(getAvatarKey(), content, null) {
            log.debug("Save avatar success")
            stream.close()
        }
    }

    private fun getAvatarKey(): String {
        val user: User = userService.getUser()
        return "avatar/${user.id}.$IMAGE_FORMAT"
    }
}
