package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.entity.FileInfo;
import com.akagiyui.drive.entity.Folder;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.UserFile;
import com.akagiyui.drive.repository.UserFileRepository;
import com.akagiyui.drive.service.UserFileService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户文件关联 服务实现类
 *
 * @author AkagiYui
 */
@Service
public class UserFileServiceImpl implements UserFileService {
    @Resource
    private UserFileRepository userFileRepository;

    @Resource
    private UserService userService;

    @Override
    public void addAssociation(User user, FileInfo fileInfo, Folder folder) {
        if (userFileRepository.existsByUserIdAndFileInfoIdAndFolder(user.getId(), fileInfo.getId(), folder)) {
            return;
        }

        UserFile userFile = new UserFile()
            .setUser(user)
            .setFileInfo(fileInfo)
            .setName(fileInfo.getName())
            .setFolder(folder);
        userFileRepository.save(userFile);
    }

    @Override
    public @NotNull List<UserFile> getFiles(@NotNull String folderId) {
        User user = userService.getUser();
        return userFileRepository.findByUserIdAndFolderId(user.getId(), folderId);
    }

    @Override
    public boolean existByFileId(@NotNull String fileId) {
        return userFileRepository.existsByFileInfoId(fileId);
    }

    @Override
    public @NotNull FileInfo getFileInfo(@NotNull String folderId) {
        User user = userService.getUser();
        UserFile association = userFileRepository.findByUserIdAndId(user.getId(), folderId);
        if (association == null) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }
        return association.getFileInfo();
    }
}
