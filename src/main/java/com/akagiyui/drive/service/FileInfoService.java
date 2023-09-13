package com.akagiyui.drive.service;

import com.akagiyui.drive.entity.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Stream;

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
     * 根据hash获取文件信息
     *
     * @param hash 文件hash
     * @return 文件信息
     */
     FileInfo getFileInfoListByHash(String hash);

     /**
      * 根据hash判断文件是否存在
      *
      * @param hash 文件hash
      * @return 文件是否存在
      */
     boolean existByHash(String hash);

     /**
      * 保存文件
      */
     List<FileInfo> saveFile(List<MultipartFile> files);

    /**
     * 记录下载
     */
    void recordDownload(FileInfo fileInfo);

    /**
     * 删除文件
     */
    void deleteFile(String id);

    /**
     * 获取所有文件信息
     */
    Stream<FileInfo> getAllFileInfo();

    /**
     * 添加文件信息
     */
    void addFileInfo(FileInfo fileInfo);
}
