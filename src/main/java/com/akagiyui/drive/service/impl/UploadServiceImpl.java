package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.component.RedisCache;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.ChunkedUploadInfo;
import com.akagiyui.drive.model.request.PreUploadRequest;
import com.akagiyui.drive.service.ConfigService;
import com.akagiyui.drive.service.UploadService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 上传 服务实现类
 *
 * @author AkagiYui
 */
@Service
public class UploadServiceImpl implements UploadService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserService userService;

    @Resource
    private ConfigService configService;

    @Override
    public boolean requestUpload(PreUploadRequest preUploadRequest) {
        // 上传文件大小限制
        long uploadFileSizeLimit = configService.getFileUploadMaxSize();
        if (preUploadRequest.getFilesize() > uploadFileSizeLimit) {
            throw new CustomException(ResponseEnum.FILE_TOO_LARGE);
        }

        User user = userService.getUser();

        // 是否已经存在上传信息
        String redisKey = "upload:" + user.getId() + ":" + preUploadRequest.getHash();
        if (redisCache.hasKey(redisKey)) {
            throw new CustomException(ResponseEnum.TASK_EXIST);
        }

        // 上传信息
        ChunkedUploadInfo chunkedInfo = new ChunkedUploadInfo(preUploadRequest);
        chunkedInfo.setUserId(user.getId());

        // 保存上传信息到redis
        redisCache.set(redisKey, chunkedInfo);

        return true;
    }
}
