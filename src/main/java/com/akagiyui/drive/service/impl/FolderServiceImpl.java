package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.entity.Folder;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.response.FolderResponse;
import com.akagiyui.drive.repository.FolderRepository;
import com.akagiyui.drive.service.FolderService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 文件夹 服务实现类
 *
 * @author AkagiYui
 */
@Service
public class FolderServiceImpl implements FolderService {

    @Resource
    private FolderRepository folderRepository;

    @Resource
    private UserService userService;

    @Override
    public @NotNull Folder createFolder(@NotNull String name, String parentId) {
        User user = userService.getUser();

        parentId = StringUtils.hasText(parentId) ? parentId : null;
        Folder parentFolder = parentId != null ? folderRepository.findById(parentId).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND)) : null;

        if (folderRepository.existsByNameAndUserIdAndParentId(name, user.getId(), parentId)) {
            throw new CustomException(ResponseEnum.FOLDER_EXIST);
        }

        Folder folder = new Folder()
            .setName(name)
            .setParent(parentFolder)
            .setUser(user);
        folderRepository.save(folder);
        return folder;
    }

    @Override
    public @NotNull Folder createFolder(@NotNull String name) {
        return createFolder(name, null);
    }

    @Override
    public @NotNull List<Folder> getSubFolders(String parentId) {
        User user = userService.getUser();
        parentId = StringUtils.hasText(parentId) ? parentId : null;

        return folderRepository.findByUserIdAndParentId(user.getId(), parentId);
    }

    @Override
    public @NotNull List<FolderResponse> getFolderChain(@NotNull String folderId) {
        User user = userService.getUser();
        if (!StringUtils.hasText(folderId)) {
            return new ArrayList<>();
        }

        Folder folder = folderRepository.findById(folderId).orElseThrow(() -> new CustomException(ResponseEnum.NOT_FOUND));
        if (!Objects.equals(folder.getUser().getId(), user.getId())) {
            throw new CustomException(ResponseEnum.NOT_FOUND);
        }

        List<FolderResponse> folderChain = new ArrayList<>();
        while (folder != null) {
            folderChain.addFirst(new FolderResponse(folder));
            folder = folder.getParent();
        }

        return folderChain;
    }
}
