package com.akagiyui.drive.service.impl;

import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.akagiyui.drive.component.CacheConstants;
import com.akagiyui.common.ResponseEnum;
import com.akagiyui.drive.entity.FileInfo;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.repository.FileInfoRepository;
import com.akagiyui.drive.service.FileInfoService;
import com.akagiyui.drive.service.StorageService;
import com.akagiyui.drive.service.UserFileService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 文件信息接口实现类
 *
 * @author AkagiYui
 */
@Service
@Slf4j
public class FileInfoServiceImpl implements FileInfoService {
    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private StorageService storageService;

    @Resource
    private UserFileService userFileService;

    @Resource
    private UserService userService;

    @Override
    @Cacheable(value = CacheConstants.FILE_INFO, key = "#id")
    public FileInfo getFileInfo(String id) {
        return fileInfoRepository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public FileInfo getFileInfoListByHash(String hash) {
        return fileInfoRepository.getFirstByHash(hash);
    }

    @Override
    public boolean existByHash(String hash) {
        return fileInfoRepository.existsByHash(hash);
    }

    private FileInfo getFileInfoWithoutCache(String id) {
        return fileInfoRepository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public List<FileInfo> saveFile(List<MultipartFile> files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            // 读取文件信息
            String filename = file.getOriginalFilename();
            long fileSize = file.getSize();
            String contentType = file.getContentType();
            byte[] fileBytes; // todo 缓存
            try {
                fileBytes = file.getInputStream().readAllBytes();
            } catch (IOException e) {
                throw new CustomException(ResponseEnum.INTERNAL_ERROR);
            }

            // 计算文件md5
            Digester digester = new Digester(DigestAlgorithm.MD5);
            String hash = digester.digestHex(fileBytes);

            // 文件未存在
            if (!fileInfoRepository.existsByHash(hash)) {
                // 新增文件记录
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(filename);
                fileInfo.setSize(fileSize);
                fileInfo.setType(contentType);
                fileInfo.setHash(hash);
                fileInfo.setStorageKey(hash); // todo 直接使用hash作为key可能不太好？
                fileInfo.setDownloadCount(0L);
                fileInfoRepository.save(fileInfo);

                // 保存二进制内容
                storageService.saveFile(fileInfo.getStorageKey(), fileBytes);
            } else {
                // 文件已存在
                log.debug("文件已存在，hash: {}", hash);
            }

            // 添加用户与文件的关联记录
            FileInfo fileInfo = fileInfoRepository.getFirstByHash(hash);
            userFileService.addAssociation(userService.getUser(), fileInfo, null);

            // 记录返回结果
            fileInfos.add(fileInfo);
        }
        return fileInfos;
    }

    @Override
    @CacheEvict(value = CacheConstants.FILE_INFO, key = "#fileInfo.id")
    public void recordDownload(FileInfo fileInfo) {
        fileInfoRepository.recordDownload(fileInfo.getId());
    }

    @Override
    @CacheEvict(value = CacheConstants.FILE_INFO, key = "#id")
    public void deleteFile(String id) {
        FileInfo fileInfo = getFileInfoWithoutCache(id);
        storageService.deleteFile(fileInfo.getStorageKey());
        fileInfoRepository.delete(fileInfo);
    }

    @Override
    public Stream<FileInfo> getAllFileInfo() {
        return fileInfoRepository.findAllByOrderByUpdateTimeAsc();
    }

    @Override
    public void addFileInfo(FileInfo fileInfo) {
        fileInfoRepository.save(fileInfo);
    }
}
