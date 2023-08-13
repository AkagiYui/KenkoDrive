package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * 本地存储服务实现类
 * @author AkagiYui
 */
@Service
@Slf4j
public class LocalStorageServiceImpl implements StorageService {
    @Value("${application.storage.local.root:./storage}")
    private String root;

    @PostConstruct
    public void init() {
        log.debug("root: " + root);
        // 检查根目录是否存在，不存在则创建
        File rootDir = new File(root);
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new RuntimeException("创建根目录失败"); // todo 自定义异常
            }
        }
    }

    @Override
    public InputStreamResource getFile(String key) {
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
    public void saveFile(String key, byte[] content) {
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
    public OutputStream saveFile(String key) {
        return saveFile(key, false);
    }

    @Override
    public OutputStream saveFile(String key, boolean overwrite) {
        File file = new File(root + File.separator + key);
        createParentDir(file);
        if (file.exists()) {
            if (!overwrite) {
                throw new RuntimeException("文件已存在");
            }
            if (!file.delete()) {
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
    public boolean exists(String key) {
        File file = new File(root + File.separator + key);
        return file.exists();
    }

    @Override
    public void deleteFile(String key) {
        File file = new File(root + File.separator + key);
        if (!file.exists()) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        if (!file.delete()) {
            throw new RuntimeException("删除文件失败");
        }
    }

    /**
     * 创建父目录
     * @param file 文件
     */
    private void createParentDir(File file) {
        File parent = file.getParentFile();
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new RuntimeException("创建父目录失败");
            }
        }
    }
}
