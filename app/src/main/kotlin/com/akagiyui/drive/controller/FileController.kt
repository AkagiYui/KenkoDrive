package com.akagiyui.drive.controller

import com.akagiyui.common.BucketManager
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.PreUploadRequest
import com.akagiyui.drive.model.response.FolderContentResponse
import com.akagiyui.drive.model.response.FolderResponse
import com.akagiyui.drive.model.response.UserFileResponse
import com.akagiyui.drive.service.*
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import org.springframework.beans.factory.DisposableBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRange
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.max

/**
 * 文件控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/file")
class FileController(
    private val storageService: StorageService,
    private val fileInfoService: FileInfoService,
    private val folderService: FolderService,
    private val userFileService: UserFileService,
    private val uploadService: UploadService,
) : DisposableBean {
    private val log by LoggerDelegate()
    val bandwidthBucketManager = BucketManager(1000 * 60 * 5)

    override fun destroy() {
        bandwidthBucketManager.close()
    }

    /**
     * 上传文件
     *
     * @param files 文件列表
     * @return 文件信息
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun upload(
        @RequestPart("file") files: List<MultipartFile>,
        folder: String?,
    ): List<UserFile> {
        return uploadService.receiveMultipartFiles(files, folder)
    }

    /**
     * 是否已存在文件
     *
     * @param hash 文件hash
     * @return 是否已存在
     */
    @GetMapping("/exist/{hash}")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun uploadStatus(@PathVariable hash: String): Boolean {
        return fileInfoService.existByHash(hash)
    }

    /**
     * 请求上传文件
     *
     * @param request 请求内容
     */
    @PostMapping("/upload/request")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun uploadRequest(@RequestBody @Validated request: PreUploadRequest) {
        uploadService.requestUpload(request)
    }

    /**
     * 上传文件分片
     */
    @PostMapping("/upload/{hash}/chunk")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun uploadChunk(
        @PathVariable("hash") fileHash: String,
        @RequestParam("file") chunk: MultipartFile,
        @RequestParam("hash") chunkHash: String,
        @RequestParam("index") chunkIndex: @Min(1) Int,
    ) {
        uploadService.uploadChunk(fileHash, chunk, chunkHash, chunkIndex)
    }

    /**
     * 获取文件列表
     *
     * @param folderId 文件夹ID
     * @return 文件列表
     */
    @GetMapping("", "/")
    @RequirePermission
    fun getFileList(@RequestParam(name = "folder", required = false) folderId: String?): FolderContentResponse {
        val files = UserFileResponse.fromUserFileList(userFileService.getFiles(folderId))
        val folders = FolderResponse.fromFolderList(folderService.getSubFolders(folderId))
        return FolderContentResponse(files, folders, folderId?.let { folderService.getFolderChain(it) } ?: emptyList())
    }

    /**
     * 获取文件信息
     *
     * @param id 文件ID
     * @return 文件信息
     */
    @GetMapping("/{id}")
    @RequirePermission
    fun getFileInfo(@PathVariable id: String): FileInfo {
        val fileInfo = fileInfoService.getFileInfo(id)
        fileInfoService.recordDownload(fileInfo) // 记录下载
        return fileInfo
    }

    /**
     * 获取文件下载临时ID
     *
     * @param userFileId 文件ID
     * @return 临时ID
     */
    @GetMapping("/{id}/token")
    @RequirePermission
    fun getTemporaryId(@PathVariable("id") userFileId: String): String {
        return userFileService.getTemporaryId(userFileId)
    }

    /**
     * 文件下载，支持断点续传
     *
     * @param temporaryId 文件临时ID
     * @param rangeString 范围
     * @param single 是否单线程下载
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("permitAll()")
    fun download(
        @PathVariable("id") temporaryId: String,
        @RequestHeader("Range", required = false) rangeString: String?,
        @RequestParam("single", defaultValue = "false") single: Boolean,
    ): ResponseEntity<StreamingResponseBody> {
        // 读取文件
        val userFile = userFileService.getFileInfoByTemporaryId(temporaryId)
        val fileInfo = userFile.fileInfo
        val fileResource = storageService.get("file/${fileInfo.storageKey}")
        val mediaLength = fileInfo.size

        // 设置响应头
        val responseHeaders = HttpHeaders().apply {
            this[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_OCTET_STREAM_VALUE
            val filename = URLEncoder.encode(userFile.name, StandardCharsets.UTF_8) // 防止中文出错
            this[HttpHeaders.CONTENT_DISPOSITION] = "attachment; filename=$filename"
            this[HttpHeaders.CONTENT_LENGTH] = "$mediaLength"
        }

        // 设置[开始位置]与[长度]
        var start = 0L
        var rangeLength = mediaLength
        val downloadPartial = !single && rangeString != null
        // 获取范围
        if (downloadPartial) {
            val ranges = HttpRange.parseRanges(rangeString)
            val range = ranges.firstOrNull()
            if (range == null) {
                log.debug("Invalid range: {}", rangeString)
                responseHeaders[HttpHeaders.CONTENT_LENGTH] = "0"
                responseHeaders[HttpHeaders.CONTENT_RANGE] = "bytes */$mediaLength"
                return ResponseEntity.status(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                    .headers(responseHeaders)
                    .body(null)
            }

            start = range.getRangeStart(mediaLength) // 开始位置
            val end = range.getRangeEnd(mediaLength) // 结束位置
            rangeLength = end - start + 1 // 范围长度
            log.debug("range: {}, length: {} Bytes", range, rangeLength)

            // 如果范围不合法，提早返回
            if (rangeLength <= 0) {
                responseHeaders[HttpHeaders.CONTENT_LENGTH] = "0"
                responseHeaders[HttpHeaders.CONTENT_RANGE] = "bytes */$mediaLength"
                return ResponseEntity.status(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                    .headers(responseHeaders)
                    .body(null)
            }
            responseHeaders[HttpHeaders.CONTENT_LENGTH] = "$rangeLength"
            responseHeaders[HttpHeaders.CONTENT_RANGE] = "bytes $start-$end/$mediaLength"
            responseHeaders[HttpHeaders.ACCEPT_RANGES] = "bytes"
        }

        val streamBody = StreamingResponseBody { outputStream: OutputStream ->
            fileResource.inputStream.use { inputStream ->
                val bytesActuallySkipped = inputStream.skip(start)
                if (bytesActuallySkipped != start) {
                    throw IOException("Skipped $bytesActuallySkipped bytes, expected $start bytes")
                }
                val speed = max(DEFAULT_BUFFER_SIZE.toLong(), 1000L * 1000 * 4)
                require(!bandwidthBucketManager.closed) {
                    "Application is shutting down"
                }
                val limiter = bandwidthBucketManager[userFile.user.id, speed]
                // 异步写入
                val data = ByteArray(DEFAULT_BUFFER_SIZE)
                var totalWritten = 0
                while (totalWritten < rangeLength) {
                    val numberOfBytesToWrite: Int = inputStream.read(data)
                    if (numberOfBytesToWrite <= 0) {
                        break
                    }
                    limiter.asBlocking().consume(numberOfBytesToWrite.toLong())
                    try {
                        outputStream.write(data, 0, numberOfBytesToWrite)
                    } catch (e: InterruptedException) {
                        log.debug("Download interrupted: {}", userFile.id)
                        break
                    }
                    totalWritten += numberOfBytesToWrite
                }
            }
            // Spring 在外部会主动进行flush，这里不需要再flush，也意味着不需要关闭流
            // https://github.com/spring-projects/spring-framework/commit/42fc4a35d59a37131bfe15d029738ab25f358241
        }

        val statusCode = if (downloadPartial) {
            HttpServletResponse.SC_PARTIAL_CONTENT
        } else {
            HttpServletResponse.SC_OK
        }
        return ResponseEntity.status(statusCode).headers(responseHeaders).body(streamBody)
    }

    /**
     * 删除文件
     *
     * @param id 文件ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission
    fun deleteFile(@PathVariable id: String) {
        fileInfoService.deleteFile(id)
    }
}
