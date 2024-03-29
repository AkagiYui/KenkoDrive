package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.entity.Folder;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.repository.FolderRepository;
import com.akagiyui.drive.service.FolderService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

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
    public Folder createFolder(String name, String parentId) {
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
    public Folder createFolder(String name) {
        return createFolder(name, null);
    }

    @Override
    public List<Folder> getSubFolders(String parentId) {
        User user = userService.getUser();
        parentId = StringUtils.hasText(parentId) ? parentId : null;

        return folderRepository.findByUserIdAndParentId(user.getId(), parentId);
    }
}
