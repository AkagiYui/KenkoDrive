package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CreateFolderException;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.model.StorageFile;
import com.akagiyui.drive.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;

/**
 * 本地存储服务实现类
 *
 * @author AkagiYui
 */
@Service
@Slf4j
public class LocalStorageServiceImpl implements StorageService {
    @Value("${application.storage.local.root:./storage}")
    private String root;

    private String tempChunkDir;

    @PostConstruct
    public void init() {
        // 检查根目录是否存在，不存在则创建
        log.debug("Local storage root dir: , {}", root);
        File rootDir = new File(root);
        if (!rootDir.exists() && (!rootDir.mkdirs())) {
            throw new CreateFolderException("Create local storage root dir failed");
        }

        // 检查临时分片目录是否存在，不存在则创建
        tempChunkDir = root + File.separator + "temp";
        File tempChunkDirFile = new File(tempChunkDir);
        if (!tempChunkDirFile.exists() && (!tempChunkDirFile.mkdirs())) {
            throw new CreateFolderException("Create temp chunk dir failed");
        }
    }

    @Override
    public @NotNull InputStreamResource getFile(@NotNull String key) {
        File file = new File(root + File.separator + key);
        if (!file.exists()) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        try {
            return new InputStreamResource(file.toURI().toURL().openStream());
        } catch (IOException e) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
    }

    @Override
    public void saveFile(@NotNull String key, byte @NotNull [] content) {
        // todo key 中可能包含路径，需要处理
        File file = new File(root + File.separator + key);
        if (file.exists()) {
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            fileOutputStream.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull OutputStream saveFile(@NotNull String key) {
        return saveFile(key, false);
    }

    @Override
    public @org.jetbrains.annotations.NotNull OutputStream saveFile(@NotNull String key, boolean overwrite) {
        File file = new File(root + File.separator + key);
        createParentDir(file);
        if (file.exists()) {
            if (!overwrite) {
                throw new RuntimeException("文件已存在");
            }
            try {
                Files.delete(file.toPath());
            } catch (IOException e) {
                throw new RuntimeException("删除文件失败");
            }
        }
        try {
            return Files.newOutputStream(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(@NotNull String key) {
        File file = new File(root + File.separator + key);
        return file.exists();
    }

    @Override
    public void deleteFile(@NotNull String key) {
        File file = new File(root + File.separator + key);
        if (!file.exists()) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException("删除文件失败");
        }
    }

    @Override
    public void saveChunk(String userId, String fileHash, int chunkIndex, byte[] content) {
        // 如果 userId 目录不存在，则创建
        File chunkFile = getChunkFile(userId, fileHash, chunkIndex);
        // 如果分片已存在，则覆盖
        if (chunkFile.exists()) {
            try {
                Files.delete(chunkFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("删除分片失败");
            }
        }
        // 保存分片
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(chunkFile);
            fileOutputStream.write(content);
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private File getChunkFile(String userId, String fileHash, int chunkIndex) {
        File userDir = new File(tempChunkDir + File.separator + userId);
        if (!userDir.exists() && (!userDir.mkdirs())) {
            throw new CreateFolderException("创建用户目录失败");
        }

        // 如果 fileHash 目录不存在，则创建
        File fileDir = new File(userDir + File.separator + fileHash);
        if (!fileDir.exists() && (!fileDir.mkdirs())) {
            throw new CreateFolderException("创建文件目录失败");
        }

        // 分片文件名为分片序号
        return new File(fileDir + File.separator + chunkIndex);
    }

    @Override
    public StorageFile mergeChunk(String userId, String fileHash, int chunkCount) {
        // 分片目录
        File fileDir = new File(tempChunkDir + File.separator + userId + File.separator + fileHash);

        // 合并后的文件
        File file = new File(fileDir + File.separator + fileHash);
        FileOutputStream fileOutputStream = getIntactFileOutputStream(chunkCount, file, fileDir);

        try {
            fileOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 移动完整文件到文件目录
        File targetFile = new File(root + File.separator + fileHash);
        createParentDir(targetFile);
        if (targetFile.exists()) {
            try {
                Files.delete(targetFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException("删除文件失败");
            }
        }
        if (!file.renameTo(targetFile)) {
            throw new RuntimeException("移动文件失败");
        }

        return new StorageFile()
            .setHash(fileHash)
            .setKey(fileHash)
            .setSize(file.length());
    }

    @NotNull
    private static FileOutputStream getIntactFileOutputStream(int chunkCount, File file, File fileDir) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // 合并分片
        for (int i = 0; i < chunkCount; i++) {
            File chunkFile = new File(fileDir + File.separator + i);
            try (FileInputStream fileInputStream = new FileInputStream(chunkFile)) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return fileOutputStream;
    }

    /**
     * 创建父目录
     *
     * @param file 文件
     */
    private void createParentDir(File file) {
        File parent = file.getParentFile();
        if (!parent.exists() && (!parent.mkdirs())) {
            throw new CreateFolderException("创建父目录失败");
        }
    }
}
