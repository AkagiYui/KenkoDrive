package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.FileInfo;
import com.akagiyui.drive.service.FileInfoService;
import com.akagiyui.drive.service.StorageService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 文件控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private StorageService storageService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private FolderService folderService;

    @Resource
    private UserFileService userFileService;

    /**
     * 上传文件
     *
     * @param files 文件列表
     * @return 文件信息
     */
    @PostMapping({"", "/"})
    public List<FileInfo> upload(@RequestParam("file") List<MultipartFile> files) {
        return fileInfoService.saveFile(files);
    }

    /**
     * 获取文件列表
     *
     * @param folderId 文件夹id
     * @return 文件列表
     */
    @GetMapping({"", "/"})
    @PreAuthorize("isAuthenticated()")
    public FolderContentResponse getFileList(@RequestParam(name = "folder", required = false) String folderId) {
        List<UserFileResponse> files = UserFileResponse.fromUserFileList(userFileService.getFiles(folderId));
        List<FolderResponse> folders = FolderResponse.fromFolderList(folderService.getSubFolders(folderId));

        return new FolderContentResponse(files, folders);
    }

    /**
     * 获取文件信息
     *
     * @param id 文件id
     * @return 文件信息
     */
    @GetMapping("/{id}")
    public FileInfo getFileInfo(@PathVariable String id) {
        return fileInfoService.getFileInfo(id);
    }

    /**
     * 下载文件，单线程
     *
     * @param id 文件id
     * @return 文件流
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String id) {
        // todo 权限校验

        // 获取文件
        FileInfo fileInfo = fileInfoService.getFileInfo(id);
        InputStreamResource fileStream = storageService.getFile(fileInfo.getStorageKey());
        fileInfoService.recordDownload(fileInfo);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 返回文件的 ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileStream);
    }

    /**
     * 断点续传文件下载
     *
     * @param id 文件id
     */
    @GetMapping("/{id}/download/multipart")
    public ResponseEntity<StreamingResponseBody> test(@PathVariable String id, @RequestHeader HttpHeaders headers) {
        // 读取文件
        FileInfo fileInfo = fileInfoService.getFileInfo(id);
        InputStreamResource fileStream = storageService.getFile(fileInfo.getStorageKey());
        fileInfoService.recordDownload(fileInfo);
        long mediaLength = fileInfo.getSize();

        // 获取范围
        // todo 支持多范围  https://zhuanlan.zhihu.com/p/620113538?utm_id=0
        List<HttpRange> ranges = headers.getRange();
        HttpRange range = ranges.isEmpty() ? null : ranges.get(0);
        log.debug("range: {}", range);
        long start = range != null ? range.getRangeStart(mediaLength) : 0; // 开始位置
        long end = range != null ? range.getRangeEnd(mediaLength) : mediaLength - 1; // 结束位置
        long rangeLength = end - start + 1; // 范围长度

        // 设置响应头
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.ACCEPT_RANGES, "bytes");
        responseHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String filename = URLEncoder.encode(fileInfo.getName(), StandardCharsets.UTF_8); // 防止中文出错
        responseHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        responseHeaders.set(HttpHeaders.CONTENT_LENGTH, "" + rangeLength);
        responseHeaders.set(HttpHeaders.CONTENT_RANGE, String.format("bytes %s-%s/%s", start, end, mediaLength));

        StreamingResponseBody streamBody;
        try {
            InputStream inputStream = fileStream.getInputStream();
            //noinspection ResultOfMethodCallIgnored
            inputStream.skip(start);
            streamBody = outputStream -> {
                // 异步写入
                int numberOfBytesToWrite;
                byte[] data = new byte[1024];
                while ((numberOfBytesToWrite = inputStream.read(data, 0, data.length)) != -1) {
                    outputStream.write(data, 0, numberOfBytesToWrite);
                }
                outputStream.flush(); // todo 必要性待验证
                outputStream.close(); // todo 必要性待验证
                inputStream.close();
            };
        } catch (IOException e) {
            return ResponseEntity.status(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE)
                    .headers(responseHeaders)
                    .body(null);
        }

        return ResponseEntity.status(HttpServletResponse.SC_PARTIAL_CONTENT)
                .headers(responseHeaders)
                .body(streamBody);
    }

    /**
     * 删除文件
     *
     * @param id 文件id
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    public boolean deleteFile(@PathVariable String id) {
        // todo 权限校验
        fileInfoService.deleteFile(id);
        return true;
    }
}
