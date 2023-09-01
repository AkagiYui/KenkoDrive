package com.akagiyui.drive.service;

import com.akagiyui.drive.model.request.PreUploadRequest;

/**
 * 上传 服务接口
 *
 * @author AkagiYui
 */
public interface UploadService {
    /**
     * 请求文件上传
     */
    boolean requestUpload(PreUploadRequest preUploadRequest);
}
