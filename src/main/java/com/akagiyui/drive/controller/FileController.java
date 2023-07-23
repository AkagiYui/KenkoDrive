package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.FileInfo;
import com.akagiyui.drive.service.FileInfoService;
import com.akagiyui.drive.service.StorageService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
     * 下载文件
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

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileInfo.getName());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);

        // 返回文件的 ResponseEntity
        return ResponseEntity.ok()
                .headers(headers)
                .body(fileStream);
    }
}
