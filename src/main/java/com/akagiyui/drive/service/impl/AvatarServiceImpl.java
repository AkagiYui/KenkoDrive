package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.component.ResponseEnum;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.exception.CustomException;
import com.akagiyui.drive.service.AvatarService;
import com.akagiyui.drive.service.StorageService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 头像服务实现类
 *
 * @author kenko
 */
@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {
    /**
     * 允许上传的头像的文件类型
     */
    public static final List<String> AVATAR_TYPES = new ArrayList<>();

    @Resource
    StorageService storageService;

    @Resource
    UserService userService;

    /* 初始化允许上传的头像的文件类型 */
    static {
        AVATAR_TYPES.add("image/jpeg");
        AVATAR_TYPES.add("image/jpg");
        AVATAR_TYPES.add("image/png");
        AVATAR_TYPES.add("image/bmp");
        AVATAR_TYPES.add("image/gif");
        AVATAR_TYPES.add("image/pjpeg");
        AVATAR_TYPES.add("image/x-png");
    }

    @Override
    public byte[] getAvatar() {
        InputStreamResource file = storageService.getFile(getAvatarKey());
        try (InputStream inputStream = file.getInputStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error("Load avatar failed", e);
            throw new CustomException(ResponseEnum.INTERNAL_ERROR);
        }
    }

    @Override
    public boolean saveAvatar(MultipartFile avatar) {
        if (avatar.isEmpty()) {
            throw new CustomException(ResponseEnum.BAD_REQUEST);
        }
        if (avatar.getSize() > AVATAR_MAX_SIZE) {
            throw new CustomException(ResponseEnum.FILE_TOO_LARGE);
        }
        if (!AVATAR_TYPES.contains(avatar.getContentType())) {
            throw new CustomException(ResponseEnum.FILE_FORMAT_NOT_SUPPORT);
        }

        BufferedImage image;
        try {
            image = ImageIO.read(avatar.getInputStream());
        } catch (IOException e) {
            log.error("Read avatar failed", e);
            throw new CustomException(ResponseEnum.INTERNAL_ERROR);
        }

        // 缩放图片并转换格式
        OutputStream outputStream = storageService.saveFile(getAvatarKey(), true);
        try {
            Thumbnails.of(image).size(WIDTH_LIMIT, HEIGHT_LIMIT).outputFormat(IMAGE_FORMAT).toOutputStream(outputStream);
            outputStream.close();
        } catch (IOException e) {
            log.error("Save avatar failed", e);
            throw new CustomException(ResponseEnum.INTERNAL_ERROR);
        }

        return true;
    }

    /**
     * 获取当前用户的头像的存储key
     */
    private String getAvatarKey() {
        User user = userService.getUser();
        return "avatar/" + user.getId() + "." + IMAGE_FORMAT;
    }
}
