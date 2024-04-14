package com.akagiyui.drive.controller;

import com.akagiyui.drive.component.permission.RequirePermission;
import com.akagiyui.drive.entity.Announcement;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.AnnouncementFilter;
import com.akagiyui.drive.model.request.AddAnnouncementRequest;
import com.akagiyui.drive.model.request.UpdateAnnouncementRequest;
import com.akagiyui.drive.model.response.AnnouncementDisplayResponse;
import com.akagiyui.drive.model.response.AnnouncementResponse;
import com.akagiyui.drive.model.response.PageResponse;
import com.akagiyui.drive.service.AnnouncementService;
import com.akagiyui.drive.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/announcement")
public class AnnouncementController {
    private final AnnouncementService announcementService;
    private final UserService userService;

    public AnnouncementController(AnnouncementService announcementService, UserService userService) {
        this.announcementService = announcementService;
        this.userService = userService;
    }

    /**
     * 新增公告
     */
    @PostMapping({"", "/"})
    @RequirePermission(Permission.ANNOUNCEMENT_ADD)
    public String addAnnouncement(@RequestBody @Validated AddAnnouncementRequest request) {
        Announcement announcement = request.toAnnouncement();
        announcement.setAuthor(userService.getUser());
        return announcementService.addAnnouncement(announcement).getId();
    }

    /**
     * 获取公告列表
     */
    @GetMapping({"", "/"})
    @RequirePermission(Permission.ANNOUNCEMENT_GET_ALL)
    public PageResponse<AnnouncementResponse> getAnnouncementList(
        @RequestParam(defaultValue = "0") Integer index,
        @RequestParam(defaultValue = "10") Integer size,
        @ModelAttribute AnnouncementFilter filter
    ) {
        Page<Announcement> announcementPage = announcementService.find(index, size, filter);
        List<Announcement> announcementList = announcementPage.getContent();
        List<AnnouncementResponse> responseList = AnnouncementResponse.fromAnnouncementList(announcementList);
        return new PageResponse<AnnouncementResponse>()
            .setPage(index)
            .setSize(size)
            .setPageCount(announcementPage.getTotalPages())
            .setTotal(announcementPage.getTotalElements())
            .setList(responseList);
    }

    /**
     * 获取用于首页展示的公告列表
     */
    @GetMapping("/index")
    @PreAuthorize("isAuthenticated()")
    public List<AnnouncementDisplayResponse> getIndexAnnouncementList() {
        return AnnouncementDisplayResponse.fromAnnouncementList(announcementService.getAnnouncementDisplayList());
    }

    /**
     * 更新公告状态
     *
     * @param id       公告id
     * @param disabled 是否关闭
     */
    @PutMapping("/{id}/status")
    @RequirePermission(Permission.ANNOUNCEMENT_UPDATE)
    public void updateStatus(@PathVariable String id, @RequestParam(required = false) Boolean disabled) {
        if (disabled != null) {
            announcementService.disable(id, disabled);
        }
    }

    /**
     * 删除公告
     *
     * @param id 公告id
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.ANNOUNCEMENT_DELETE)
    public void deleteAnnouncement(@PathVariable String id) {
        announcementService.delete(id);
    }

    /**
     * 修改公告
     *
     * @param id      公告id
     * @param request 更新请求
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.ANNOUNCEMENT_UPDATE)
    public void updateAnnouncement(@PathVariable String id, @Validated @RequestBody UpdateAnnouncementRequest request) {
        announcementService.update(id, request);
    }
}
