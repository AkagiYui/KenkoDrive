package com.akagiyui.drive.service;

import com.akagiyui.drive.model.request.PreUploadRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传 服务接口
 *
 * @author AkagiYui
 */
public interface UploadService {
    /**
     * 请求文件上传
     */
    void requestUpload(PreUploadRequest preUploadRequest);

    /**
     * 上传分片
     */
    void uploadChunk(String fileHash, MultipartFile chunk, String chunkHash, int chunkIndex);
}
