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
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.constraints.Min
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.IOException
import java.io.OutputStream
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

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
    fun upload(@RequestParam("file") files: List<MultipartFile>): List<FileInfo> {
        return fileInfoService.saveFile(files)
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
    fun getFileInfo(@PathVariable id: String): FileInfo {
        return fileInfoService.getFileInfo(id)
    }

    /**
     * 下载文件，单线程
     *
     * @param id 文件id
     * @return 文件流
     */
    @GetMapping("/{id}/download/single")
    fun downloadFile(@PathVariable id: String?): ResponseEntity<InputStreamResource> {
        // 获取文件
        val fileInfo = userFileService.getFileInfo(id!!)

        val fileStream = storageService.getFile(fileInfo.storageKey)
        fileInfoService.recordDownload(fileInfo)

        // 设置响应头
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.name)
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)

        // 返回文件的 ResponseEntity
        return ResponseEntity.ok()
            .headers(headers)
            .body(fileStream)
    }

    /**
     * 断点续传文件下载
     *
     * @param id 文件id
     */
    @GetMapping("/{id}/download")
    fun test(@PathVariable id: String?, @RequestHeader headers: HttpHeaders): ResponseEntity<StreamingResponseBody> {
        // 读取文件
        val fileInfo = userFileService.getFileInfo(id!!)
        val fileStream = storageService.getFile(fileInfo.storageKey)
        fileInfoService.recordDownload(fileInfo)
        val mediaLength = fileInfo.size

        // 获取范围
        // todo 支持多范围  https://zhuanlan.zhihu.com/p/620113538?utm_id=0
        val ranges = headers.range
        val range = if (ranges.isEmpty()) null else ranges.first()
        log.debug("range: {}", range)
        val start = range?.getRangeStart(mediaLength) ?: 0 // 开始位置
        val end = range?.getRangeEnd(mediaLength) ?: (mediaLength - 1) // 结束位置
        val rangeLength = end - start + 1 // 范围长度

        // 设置响应头
        val responseHeaders = HttpHeaders()
        responseHeaders[HttpHeaders.ACCEPT_RANGES] = "bytes"
        responseHeaders[HttpHeaders.CONTENT_TYPE] = MediaType.APPLICATION_OCTET_STREAM_VALUE
        val filename = URLEncoder.encode(fileInfo.name, StandardCharsets.UTF_8) // 防止中文出错
        responseHeaders[HttpHeaders.CONTENT_DISPOSITION] = "attachment; filename=$filename"
        responseHeaders[HttpHeaders.CONTENT_LENGTH] = "" + rangeLength
        responseHeaders[HttpHeaders.CONTENT_RANGE] =
            String.format("bytes %s-%s/%s", start, end, mediaLength)

        val streamBody: StreamingResponseBody
        try {
            val inputStream = fileStream.inputStream
            val bytesActuallySkipped = inputStream.skip(start)
            if (bytesActuallySkipped != start) {
                throw IOException("Skipped $bytesActuallySkipped bytes, expected $start bytes")
            }
            streamBody = StreamingResponseBody { outputStream: OutputStream ->
                // 异步写入
                var numberOfBytesToWrite: Int
                val data = ByteArray(1024)
                while ((inputStream.read(data, 0, data.size).also { numberOfBytesToWrite = it }) != -1) {
                    outputStream.write(data, 0, numberOfBytesToWrite)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            }
        } catch (e: IOException) {
            return ResponseEntity.status(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                .headers(responseHeaders)
                .body(null)
        }

        return ResponseEntity.status(HttpServletResponse.SC_PARTIAL_CONTENT)
            .headers(responseHeaders)
            .body(streamBody)
    }

    /**
     * 删除文件
     *
     * @param id 文件id
     */
    @DeleteMapping("/{id}")
    fun deleteFile(@PathVariable id: String?) {
        fileInfoService.deleteFile(id!!)
    }
}
