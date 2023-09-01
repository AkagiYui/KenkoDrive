package com.akagiyui.drive.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 预上传文件 请求
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class PreUploadRequest {
    /**
     * 整个文件hash
     */
    @NotNull
    @Size(min = 32, max = 32)
    private String hash;

    /**
     * 预期分片大小，单位：字节Byte
     */
    private int chunkSize;

    /**
     * 分片数量
     */
    @Min(1)
    private int chunkCount;

    /**
     * 文件名
     */
    @NotNull
    @NotBlank
    private String filename;

    /**
     * 文件大小
     */
    @Min(1)
    private long filesize;
}
