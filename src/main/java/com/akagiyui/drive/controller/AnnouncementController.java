package com.akagiyui.drive.controller;

import com.akagiyui.drive.component.permission.RequirePermission;
import com.akagiyui.drive.entity.Announcement;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.request.AddAnnouncementRequest;
import com.akagiyui.drive.model.response.AnnouncementDisplayResponse;
import com.akagiyui.drive.model.response.AnnouncementResponse;
import com.akagiyui.drive.service.AnnouncementService;
import com.akagiyui.drive.service.UserService;
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
    public void addAnnouncement(@RequestBody @Validated AddAnnouncementRequest request) {
        Announcement announcement = request.toAnnouncement();
        announcement.setAuthor(userService.getUser());
        announcementService.addAnnouncement(announcement);
    }

    /**
     * 获取公告列表
     */
    @GetMapping({"", "/"})
    @RequirePermission(Permission.ANNOUNCEMENT_GET_ALL)
    public List<AnnouncementResponse> getAnnouncementList(@RequestParam(defaultValue = "false") Boolean all) {
        return AnnouncementResponse.fromAnnouncementList(announcementService.getAnnouncementList(all));
    }

    /**
     * 获取用于首页展示的公告列表
     */
    @GetMapping("/index")
    @PreAuthorize("isAuthenticated()")
    public List<AnnouncementDisplayResponse> getIndexAnnouncementList() {
        return AnnouncementDisplayResponse.fromAnnouncementList(announcementService.getAnnouncementDisplayList());
    }

}
