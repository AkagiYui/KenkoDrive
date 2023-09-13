package com.akagiyui.drive.model;

import com.akagiyui.drive.model.request.PreUploadRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分块文件上传信息
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class ChunkedUploadInfo implements Serializable {
    /**
     * 整个文件hash
     */
    private String hash;

    /**
     * 预期分片大小，单位：字节Byte
     */
    private int chunkSize;

    /**
     * 分片数量
     */
    private int chunkCount;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 文件大小
     */
    private long filesize;

    /**
     * 分片信息 列表
     */
    private List<Chunk> chunks;

    /**
     * 分片信息
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Chunk implements Serializable {
        /**
         * 分片序号
         */
        private int index;

        /**
         * 分片大小
         */
        private int size;

        /**
         * 分片hash
         */
        private String hash;

        /**
         * 校验成功
         */
        private boolean checkSuccess;
    }

    public ChunkedUploadInfo(PreUploadRequest request) {
        this.hash = request.getHash();
        this.chunkSize = request.getChunkSize();
        this.chunkCount = request.getChunkCount();
        this.filename = request.getFilename();
        this.filesize = request.getFilesize();

        // 初始化分片信息
        this.chunks = new ArrayList<>(chunkCount);
        for (int i = 0; i < chunkCount; i++) {
            chunks.add(new Chunk(i, 0, null, false));
        }
    }

    /**
     * 是否上传完成
     */
    @JsonIgnore
    public boolean isUploadFinish() {
        for (Chunk chunk : chunks) {
            if (!chunk.isCheckSuccess()) {
                return false;
            }
        }
        return true;
    }
}
