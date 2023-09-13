package com.akagiyui.drive.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.component.RedisCache;
import com.akagiyui.drive.entity.FileInfo;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.ChunkedUploadInfo;
import com.akagiyui.drive.model.StorageFile;
import com.akagiyui.drive.model.request.PreUploadRequest;
import com.akagiyui.drive.service.*;
import jakarta.annotation.Resource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private StorageService storageService;

    @Resource
    private FileInfoService fileInfoService;

    @Resource
    private UserFileService userFileService;

    private boolean isInfoInRedis(String userId, String hash) {
        String redisKey = "upload:" + userId + ":" + hash;
        return redisCache.hasKey(redisKey);
    }

    private void saveInfoToRedis(String userId, String hash, ChunkedUploadInfo chunkedInfo) {
        String redisKey = "upload:" + userId + ":" + hash;
        redisCache.set(redisKey, chunkedInfo);
    }

    private ChunkedUploadInfo getInfoFromRedis(String userId, String hash) {
        String redisKey = "upload:" + userId + ":" + hash;
        return redisCache.get(redisKey);
    }

    @Override
    public boolean requestUpload(PreUploadRequest preUploadRequest) {
        // 上传文件大小限制
        long uploadFileSizeLimit = configService.getFileUploadMaxSize();
        if (preUploadRequest.getFilesize() > uploadFileSizeLimit) {
            throw new CustomException(ResponseEnum.FILE_TOO_LARGE);
        }

        User user = userService.getUser();

        // 是否已经存在上传信息
        if (isInfoInRedis(user.getId(), preUploadRequest.getHash())) {
            throw new CustomException(ResponseEnum.TASK_EXIST);
        }

        // 上传信息
        ChunkedUploadInfo chunkedInfo = new ChunkedUploadInfo(preUploadRequest);
        chunkedInfo.setUserId(user.getId());

        // 保存上传信息到redis
        saveInfoToRedis(user.getId(), preUploadRequest.getHash(), chunkedInfo);
        return true;
    }

    @Override
    public void uploadChunk(String fileHash, MultipartFile chunk, String chunkHash, int chunkIndex) {
        if (chunk.isEmpty()) {
            throw new CustomException(ResponseEnum.BAD_REQUEST);
        }
        if (chunkIndex < 0) {
            throw new CustomException(ResponseEnum.BAD_REQUEST);
        }
        if (!StringUtils.hasText(chunkHash)) {
            throw new CustomException(ResponseEnum.BAD_REQUEST);
        }

        User user = userService.getUser();
        if (!isInfoInRedis(user.getId(), fileHash)) {
            throw new CustomException(ResponseEnum.TASK_NOT_FOUND);
        }

        ChunkedUploadInfo chunkedInfo = getInfoFromRedis(user.getId(), fileHash);
        if (chunkedInfo.isUploadFinish()) {
            throw new CustomException(ResponseEnum.TASK_NOT_FOUND);
        }

        // 校验分片
        byte[] chunkBytes;
        try {
            chunkBytes = chunk.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String realMd5 = DigestUtil.md5Hex(chunkBytes);
        if (!Objects.equals(realMd5, chunkHash)) {
            throw new CustomException(ResponseEnum.VERIFY_FAILED);
        }

        // 保存分片信息
        List<ChunkedUploadInfo.Chunk> chunksInfo = chunkedInfo.getChunks();
        ChunkedUploadInfo.Chunk chunkInfo = chunksInfo.get(chunkIndex);
        chunkInfo.setHash(chunkHash);
        chunkInfo.setCheckSuccess(true);
        saveInfoToRedis(user.getId(), fileHash, chunkedInfo);

        storageService.saveChunk(user.getId(), fileHash, chunkIndex, chunkBytes);
        if (chunkedInfo.isUploadFinish()){
            taskExecutor.execute(() -> {
                // 合并分片
                StorageFile storageFile = storageService.mergeChunk(user.getId(), fileHash, chunkedInfo.getChunkCount());
                // todo 校验整个文件的md5
                // 保存文件信息
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(chunkedInfo.getFilename());
                fileInfo.setSize(chunkedInfo.getFilesize());
                fileInfo.setType(storageFile.getType());
                fileInfo.setHash(fileHash);
                fileInfo.setStorageKey(storageFile.getKey());
                fileInfoService.addFileInfo(fileInfo);
                // 添加用户文件关联
                userFileService.addAssociation(user, fileInfo, null);
            });
        }
    }

    @Resource
    private TaskExecutor taskExecutor;
}
