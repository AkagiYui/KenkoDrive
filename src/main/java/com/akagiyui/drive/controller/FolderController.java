package com.akagiyui.drive.controller;

import com.akagiyui.drive.model.request.CreateFolderRequest;
import com.akagiyui.drive.model.response.FolderResponse;
import com.akagiyui.drive.service.FolderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文件夹 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/folder")
public class FolderController {

    @Resource
    private FolderService folderService;

    /**
     * 获取文件夹列表
     *
     * @param parentId 父文件夹ID
     * @return 文件夹列表
     */
    @GetMapping({"", "/"})
    public List<FolderResponse> listFolder(@RequestParam(name = "parent", required = false) String parentId) {
        return FolderResponse.fromFolderList(folderService.getSubFolders(parentId));
    }

    /**
     * 创建文件夹
     *
     * @param parentId 父文件夹ID
     * @param name     文件夹名
     * @return 文件夹信息
     */
    @PostMapping({"", "/"})
    public FolderResponse createFolder(@RequestBody CreateFolderRequest request) {
        return new FolderResponse(folderService.createFolder(request.getName(), request.getParent()));
    }
}
