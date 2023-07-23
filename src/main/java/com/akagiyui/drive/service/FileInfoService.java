package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件信息接口
 *
 * @author AkagiYui
 */
public interface FileInfoService {
    /**
     * 获取文件
     */
     FileInfo getFileInfo(String id);

     /**
      * 保存文件
      */
     List<FileInfo> saveFile(List<MultipartFile> files);
}
