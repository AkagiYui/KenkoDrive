package com.akagiyui.drive.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 头像服务接口
 * @author kenko
 */
public interface AvatarService {
    /**
     * 头像文件大小的上限值(3MB)
     */
    int AVATAR_MAX_SIZE = 3 * 1024 * 1024;

    /**
     * 高度限制
     */
    int HEIGHT_LIMIT = 200;

    /**
     * 宽度限制
     */
    int WIDTH_LIMIT = 200;

    /**
     * 头像图片格式
     */
    String IMAGE_FORMAT = "jpg";

    /**
     * 获取头像
     * @return 头像
     */
    byte[] getAvatar();

    /**
     * 保存头像
     * @param avatar 头像
     * @return 保存结果
     */
    boolean saveAvatar(MultipartFile avatar);
}
