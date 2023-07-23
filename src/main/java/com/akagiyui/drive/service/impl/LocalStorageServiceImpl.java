package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.component.ResponseEnum;
import com.akagiyui.drive.exception.CustomException;
import com.akagiyui.drive.service.StorageService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 本地存储服务实现类
 * @author AkagiYui
 */
@Service
public class LocalStorageServiceImpl implements StorageService {
    @Value("${application.storage.local.root:./storage}")
    private String root;

    @PostConstruct
    public void init() {
        System.out.println("root: " + root);
        // 检查根目录是否存在，不存在则创建
        File rootDir = new File(root);
        if (!rootDir.exists()) {
            if (!rootDir.mkdirs()) {
                throw new RuntimeException("创建根目录失败");
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
}
