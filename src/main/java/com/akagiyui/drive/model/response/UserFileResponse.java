package com.akagiyui.drive.model.response;

import com.akagiyui.drive.entity.UserFile;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 用户文件信息 响应实体
 *
 * @author AkagiYui
 */
@Data
@Accessors(chain = true)
public class UserFileResponse {
    /**
     * 文件id
     */
    private String id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 文件大小（字节Byte）
     */
    private long size;

    /**
     * 文件类型
     */
    private String type;

    public UserFileResponse(UserFile userFile) {
        this.id = userFile.getId();
        this.name = userFile.getName();
        this.size = userFile.getFileInfo().getSize();
        this.type = userFile.getFileInfo().getType();
    }

    /**
     * 将文件列表转换为文件响应列表
     *
     * @param userFiles 文件 列表
     * @return 文件响应 列表
     */
    public static List<UserFileResponse> fromUserFileList(List<UserFile> userFiles) {
        return userFiles.stream().map(UserFileResponse::new).toList();
    }
}
