package com.akagiyui.drive.controller

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.drive.component.permission.RequirePermission
import com.akagiyui.drive.entity.FileInfo
import com.akagiyui.drive.model.Permission
import com.akagiyui.drive.model.request.PreUploadRequest
import com.akagiyui.drive.model.response.FolderContentResponse
import com.akagiyui.drive.model.response.FolderResponse
import com.akagiyui.drive.model.response.UserFileResponse
import com.akagiyui.drive.service.*
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketListener
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
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
) {
    private val log by LoggerDelegate()

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
    ): List<FileInfo> {
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
     * @param folderId 文件夹id
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
     * @param id 文件id
     * @return 文件信息
     */
    @GetMapping("/{id}")
    @RequirePermission
    fun getFileInfo(@PathVariable id: String): FileInfo {
        return fileInfoService.getFileInfo(id)
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
     * 下载带宽限流器
     */
    val secondBandwidthLimiters = ConcurrentHashMap<String, Bucket>()

    /**
     * 限流器最后访问时间
     */
    val limiterLastAccessTime = ConcurrentHashMap<String, Long>()

    @Scheduled(fixedDelay = 5000)
    fun cleanBandwidthLimiters() {
        log.trace("Clean idle bandwidth limiters")
        val now = System.currentTimeMillis()
        limiterLastAccessTime.forEach { (userId, lastAccessTime) ->
            if (now - lastAccessTime > 1000 * 60 * 5) { // 5分钟未使用的限流器释放
                log.debug("Remove idle bandwidth limiter for user: {}", userId)
                secondBandwidthLimiters.remove(userId)
                limiterLastAccessTime.remove(userId)
            }
        }
    }

    class BandwidthBucketListener(
        private val key: String,
        private val limiterLastAccessTime: ConcurrentHashMap<String, Long>,
    ) : BucketListener {
        override fun onConsumed(tokens: Long) {
            limiterLastAccessTime[key] = System.currentTimeMillis()
        }

        override fun onRejected(tokens: Long) {}

        override fun onParked(nanos: Long) {}

        override fun onInterrupted(e: InterruptedException?) {}

        override fun onDelayed(nanos: Long) {}
    }

    /**
     * 文件下载，支持断点续传
     *
     * @param temporaryId 文件临时ID
     */
    @GetMapping("/{id}/download")
    @PreAuthorize("permitAll()")
    fun download(
        @PathVariable("id") temporaryId: String,
        @RequestHeader headers: HttpHeaders,
    ): ResponseEntity<StreamingResponseBody> {
        // 读取文件
        val userFile = userFileService.getFileInfoByTemporaryId(temporaryId)
        val fileInfo = userFile.fileInfo
        val fileResource = storageService.get("file/${fileInfo.storageKey}")
        fileInfoService.recordDownload(fileInfo) // TODO 断点续传导致下载次数不准确
        val mediaLength = fileInfo.size
        // 获取范围
        // todo 支持多范围  https://zhuanlan.zhihu.com/p/620113538?utm_id=0
        val ranges = headers.range
        val range = if (ranges.isEmpty()) null else ranges.first()
        log.debug("range: {}, length: {} Bytes", range, mediaLength)
        val start = range?.getRangeStart(mediaLength) ?: 0 // 开始位置
        val end = range?.getRangeEnd(mediaLength) ?: (mediaLength - 1) // 结束位置
        val rangeLength = end - start + 1 // 范围长度

        // 设置响应头
        val responseHeaders = HttpHeaders().apply {
            this[HttpHeaders.ACCEPT_RANGES] = "bytes"
            this[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_OCTET_STREAM_VALUE
            val filename = URLEncoder.encode(userFile.name, StandardCharsets.UTF_8) // 防止中文出错
            this[HttpHeaders.CONTENT_DISPOSITION] = "attachment; filename=$filename"
            this[HttpHeaders.CONTENT_LENGTH] = rangeLength.toString()
            this[HttpHeaders.CONTENT_RANGE] = String.format("bytes %s-%s/%s", start, end, mediaLength)
        }

        return try {
            val streamBody = StreamingResponseBody { outputStream: OutputStream ->
                fileResource.inputStream.use { inputStream ->
                    val bytesActuallySkipped = inputStream.skip(start)
                    if (bytesActuallySkipped != start) {
                        throw IOException("Skipped $bytesActuallySkipped bytes, expected $start bytes")
                    }
                    val bandwidthLimiter = secondBandwidthLimiters.computeIfAbsent(userFile.user.id) {
                        val speed: Long = max(DEFAULT_BUFFER_SIZE.toLong(), 1024L * 1024 * 2) // 2MB/s
                        log.debug("Create bandwidth limiter for user: {}, speed: {} Bytes/s", userFile.user.id, speed)
                        Bucket.builder()
                            .addLimit {
                                it.capacity(speed)
                                    .refillGreedy(speed, Duration.ofSeconds(1))
                                    .initialTokens(speed)
                            }
                            .withListener(BandwidthBucketListener(userFile.user.id, limiterLastAccessTime))
                            .build()
                    }
                    // 异步写入
                    var numberOfBytesToWrite: Int
                    val data = ByteArray(DEFAULT_BUFFER_SIZE)
                    try {
                        while ((inputStream.read(data, 0, data.size).also { numberOfBytesToWrite = it }) != -1) {
                            bandwidthLimiter.asBlocking().consume(numberOfBytesToWrite.toLong())
                            outputStream.write(data, 0, numberOfBytesToWrite)
                        }
                    } catch (e: InterruptedException) {
                        log.debug("Download interrupted: {}", userFile.id)
                    }
                }
                // Spring 在外部会主动进行flush，这里不需要再flush，也意味着不需要关闭流
                // https://github.com/spring-projects/spring-framework/commit/42fc4a35d59a37131bfe15d029738ab25f358241
            }
            ResponseEntity.status(HttpServletResponse.SC_PARTIAL_CONTENT)
                .headers(responseHeaders)
                .body(streamBody)

        } catch (e: IOException) {
            ResponseEntity.status(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                .headers(responseHeaders)
                .body(null)
        }
    }

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    @DeleteMapping("/{id}")
    @RequirePermission
    fun deleteFile(@PathVariable id: String) {
        fileInfoService.deleteFile(id)
    }
}
