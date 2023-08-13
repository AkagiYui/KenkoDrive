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
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    @Cacheable(value = CacheConstants.FILE_INFO, key = "#id")
    public FileInfo getFileInfo(String id) {
        return fileInfoRepository.findById(id).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
    }

    @Override
    public List<FileInfo> saveFile(List<MultipartFile> files) {
        List<FileInfo> fileInfos = new ArrayList<>();
        for (MultipartFile file : files) {
            String filename = file.getOriginalFilename();
            long fileSize = file.getSize();
            String contentType = file.getContentType();
            byte[] fileBytes; // todo 缓存
            try {
                fileBytes = file.getInputStream().readAllBytes();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // 计算文件md5
            Digester digester = new Digester(DigestAlgorithm.MD5);
            String hash = digester.digestHex(fileBytes);

            if (fileInfoRepository.existsByHash(hash)) {
                log.info("文件已存在，hash: {}", hash);
                fileInfos.add(fileInfoRepository.getFirstByHash(hash));
                continue;
            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.setName(filename);
            fileInfo.setSize(fileSize);
            fileInfo.setType(contentType);
            fileInfo.setHash(hash);
            fileInfo.setStorageKey(hash); // todo 直接使用hash作为key可能不太好？
            fileInfo.setDownloadCount(0L);
            fileInfo.setRefCount(0L);
            fileInfoRepository.save(fileInfo);
            fileInfos.add(fileInfoRepository.getFirstByHash(hash));

            // 保存文件
            storageService.saveFile(fileInfo.getStorageKey(), fileBytes);
        }
        return fileInfos;
    }

    @Override
    @CacheEvict(value = CacheConstants.FILE_INFO, key = "#fileInfo.id")
    public void recordDownload(FileInfo fileInfo) {
        fileInfo.setDownloadCount(fileInfo.getDownloadCount() + 1);
        fileInfoRepository.save(fileInfo);
    }

    @Override
    @CacheEvict(value = CacheConstants.FILE_INFO, key = "#id")
    public void deleteFile(String id) {
        FileInfo fileInfo = getFileInfo(id);
        storageService.deleteFile(fileInfo.getStorageKey());
        fileInfoRepository.delete(fileInfo);
    }
}
