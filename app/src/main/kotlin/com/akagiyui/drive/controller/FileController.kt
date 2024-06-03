package com.akagiyui.drive.controller

import com.akagiyui.common.BucketManager
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.component.CurrentUser
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.entity.User
import com.akagiyui.drive.entity.UserFile
import com.akagiyui.drive.model.FileInfoFilter
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.CreateUploadTaskRequest
import com.akagiyui.drive.model.request.MirrorFileRequest
import com.akagiyui.drive.model.response.FileInfoResponse
import com.akagiyui.drive.model.response.PageResponse
import com.akagiyui.drive.model.response.UploadTaskResponse
import com.akagiyui.drive.model.response.toResponse
import com.akagiyui.drive.service.FileInfoService
import com.akagiyui.drive.service.StorageService
import com.akagiyui.drive.service.UploadService
import com.akagiyui.drive.service.UserFileService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.beans.factory.DisposableBean
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpRange
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
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
    private val userFileService: UserFileService,
    private val uploadService: UploadService,
) : DisposableBean {
    private val log by LoggerDelegate()
    val speedBucketManager = BucketManager(1000 * 60 * 5) // 限速桶管理器，5分钟清理一次
    val bandwidthBucketManager = BucketManager(1000 * 60 * 60 * 24 * 2) // 带宽桶管理器，2天清理一次

    override fun destroy() {
        speedBucketManager.close()
        bandwidthBucketManager.close()
    }

    /**
     * 获取文件列表
     */
    @GetMapping
    @RequirePermission(Permission.FILE_LIST_ALL)
    fun getFileList(
        @RequestParam(defaultValue = "0") index: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @ModelAttribute filter: FileInfoFilter?,
    ): PageResponse<FileInfoResponse> {
        val page = fileInfoService.find(index, size, filter)
        return PageResponse(page, page.content.toResponse())
    }

    /**
     * 上传文件（单连接）
     *
     * @param files 文件列表
     * @return 文件信息
     */
    @PostMapping("", "/")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun upload(
        @RequestPart("file") files: List<MultipartFile>,
        folder: String?,
        @CurrentUser user: User,
    ): List<UserFile> {
        return uploadService.receiveMultipartFiles(user, files, folder)
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
     * 秒传
     * @param request 请求内容
     * @param user 用户
     * @return 是否成功
     */
    @PostMapping("/mirror")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun mirrorUpload(@RequestBody request: MirrorFileRequest, @CurrentUser user: User): Boolean {
        return try {
            userFileService.mirrorFile(user, request)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 创建上传任务
     *
     * @param request 请求内容
     */
    @PostMapping("/task")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun createUploadTask(@RequestBody @Validated request: CreateUploadTaskRequest, @CurrentUser user: User): String {
        return uploadService.createUploadTask(user, request).id
    }

    /**
     * 上传文件分片
     *
     * @param taskId 任务ID
     * @param chunk 分片文件
     * @param chunkHash 分片哈希
     * @param chunkIndex 分片索引
     * @return 是否已收到该任务的所有分片
     */
    @PostMapping("/task/{id}/chunk")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun uploadChunk(
        @PathVariable("id") taskId: String,
        @RequestParam("blob") chunk: MultipartFile,
        @RequestParam("hash") @Validated @Size(min = 64, max = 64) chunkHash: String,
        @RequestParam("index") @Validated @Min(0) chunkIndex: Int,
        @CurrentUser user: User,
    ): Boolean {
        return uploadService.uploadChunk(user, taskId, chunk, chunkHash, chunkIndex)
    }

    /**
     * 获取上传任务信息
     *
     * @param taskId 任务ID
     * @return 任务信息
     */
    @GetMapping("/task/{id}")
    @RequirePermission(Permission.PERSONAL_UPLOAD)
    fun getUploadTaskInfo(@PathVariable("id") taskId: String): UploadTaskResponse {
        return UploadTaskResponse(uploadService.getUploadTask(taskId))
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
    fun createTemporaryId(@PathVariable("id") userFileId: String, @CurrentUser user: User): String {
        val (randomId, userFile) = userFileService.createTemporaryId(user.id, userFileId)
        fileInfoService.recordDownload(userFile.fileInfo.id) // 记录下载
        return randomId
    }

    /**
     * 文件下载，支持断点续传
     *
     * @param temporaryId 文件临时ID
     * @param rangeString 范围
     * @param single 是否下载整个文件
     */
    @GetMapping("/{id}/download/**")
    @PreAuthorize("permitAll()")
    fun download(
        @PathVariable("id") temporaryId: String,
        @RequestHeader("Range", required = false) rangeString: String?,
        @RequestParam("single", defaultValue = "false") single: Boolean,
    ): ResponseEntity<StreamingResponseBody> {
        // 读取文件
        val userFile = userFileService.getFileInfoByTemporaryId(temporaryId)
        val fileInfo = userFile.fileInfo
        if (fileInfo.locked) {
            return ResponseEntity.status(HttpServletResponse.SC_FORBIDDEN).body(null)
        }
        val fileResource = storageService.get(fileInfo.storageKey)
        val mediaLength = fileInfo.size

        // 设置响应头
        val responseHeaders = HttpHeaders().apply {
            this[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_OCTET_STREAM_VALUE // 二进制流
            val filename = URLEncoder.encode(userFile.name, StandardCharsets.UTF_8) // 防止中文出错
            this[HttpHeaders.CONTENT_DISPOSITION] = "attachment; filename=$filename" // 下载文件名
            this[HttpHeaders.CONTENT_LENGTH] = "$mediaLength" // 文件长度
        }

        var statusCode = HttpServletResponse.SC_OK // 状态码
        var start = 0L // 开始位置
        var rangeLength = mediaLength // 范围长度
        if (!single && rangeString != null) { // 获取范围，修改[开始位置]、[长度]与[状态码]
            try {
                val ranges = HttpRange.parseRanges(rangeString)
                val range = ranges.firstOrNull()
                checkNotNull(range)
                start = range.getRangeStart(mediaLength) // 开始位置
                val end = range.getRangeEnd(mediaLength) // 结束位置
                rangeLength = end - start + 1 // 范围长度
                log.debug("range: {}, length: {} Bytes", range, rangeLength)

                check(rangeLength > 0) // 如果范围不合法，提早返回
                responseHeaders[HttpHeaders.CONTENT_LENGTH] = "$rangeLength"
                responseHeaders[HttpHeaders.CONTENT_RANGE] = "bytes $start-$end/$mediaLength"
                responseHeaders[HttpHeaders.ACCEPT_RANGES] = "bytes"
                statusCode = HttpServletResponse.SC_PARTIAL_CONTENT
            } catch (e: IllegalStateException) {
                log.debug("Invalid range: {}", rangeString)
                responseHeaders[HttpHeaders.CONTENT_LENGTH] = "0"
                responseHeaders[HttpHeaders.CONTENT_RANGE] = "bytes */$mediaLength"
                return ResponseEntity.status(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                    .headers(responseHeaders)
                    .body(null)
            }
        }

        val streamBody = StreamingResponseBody { writeStream(it, fileResource, userFile.user.id, start, rangeLength) }
        return ResponseEntity.status(statusCode).headers(responseHeaders).body(streamBody)
    }

    /**
     * 异步写入下载流
     *
     * @param outputStream 输出流
     * @param fileResource 文件资源
     * @param bucketKey 限速桶Key
     * @param start 文件开始位置
     * @param rangeLength 期望写入长度
     */
    private fun writeStream(
        outputStream: OutputStream,
        fileResource: InputStreamResource,
        bucketKey: String,
        start: Long,
        rangeLength: Long,
    ) {
        fileResource.inputStream.use { inputStream ->
            val bytesActuallySkipped = inputStream.skip(start)
            check(bytesActuallySkipped == start) { "Skipped $bytesActuallySkipped bytes, expected $start bytes" }
            val speed = max(DEFAULT_BUFFER_SIZE.toLong(), 1000L * 1000 * 4) // 用户上限速度，4MB/s
            check(!speedBucketManager.closed && !bandwidthBucketManager.closed) {
                "Application is shutting down" // 应用正在关闭，提前结束下载
            }
            val speedLimiter = speedBucketManager[bucketKey, speed] // 速度限速器
            val bandwidthLimiter = bandwidthBucketManager[
                bucketKey,
                1000L * 1000 * 1000 * 10, // 10GB
                Duration.ofDays(1) // 1天
            ] // 流量限制器
            val data = ByteArray(DEFAULT_BUFFER_SIZE) // 缓冲区
            var totalWritten = 0 // 已写入数据量
            while (totalWritten < rangeLength) { // 限制写入数据量
                val numberOfBytesToWrite: Int = inputStream.read(data)
                if (numberOfBytesToWrite <= 0) break
                speedLimiter.asBlocking().consume(numberOfBytesToWrite.toLong())
                bandwidthLimiter.asBlocking().consume(numberOfBytesToWrite.toLong())
                try {
                    outputStream.write(data, 0, numberOfBytesToWrite)
                } catch (e: InterruptedException) {
                    log.debug("Download interrupted")
                    break
                }
                totalWritten += numberOfBytesToWrite
            }
        }
        // Spring 在外部会主动进行flush，这里不需要再flush，也意味着不需要关闭流
        // https://github.com/spring-projects/spring-framework/commit/42fc4a35d59a37131bfe15d029738ab25f358241
    }

    /**
     * 删除文件
     *
     * @param id 用户文件ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission
    fun deleteFile(@PathVariable id: String, @CurrentUser user: User) {
        userFileService.userDeleteFile(user.id, id)
    }

    /**
     * 移动文件
     * @param id 用户文件ID
     * @param folderId 目标文件夹ID
     * @param user 用户
     */
    @PutMapping("/{id}/move")
    @RequirePermission
    fun moveFile(
        @PathVariable id: String,
        @RequestParam("folder", required = false) folderId: String?,
        @CurrentUser user: User,
    ) {
        userFileService.moveFile(user.id, id, folderId)
    }

    /**
     * 重命名文件
     * @param id 用户文件ID
     * @param name 新文件名
     * @param user 用户
     */
    @PutMapping("/{id}/name")
    @RequirePermission
    fun renameFile(
        @PathVariable id: String,
        @RequestParam("name") @Validated @NotBlank name: String,
        @CurrentUser user: User,
    ) {
        userFileService.rename(user.id, id, name)
    }


    /**
     * 锁定/解锁文件
     * @param id 文件ID
     * @param locked 是否锁定
     */
    @PutMapping("/{id}/lock")
    @RequirePermission(Permission.FILE_LOCK)
    fun lockFile(
        @PathVariable id: String,
        @RequestParam("locked") locked: Boolean,
    ) {
        fileInfoService.lock(id, locked)
    }
}
