package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.Announcement;
import com.akagiyui.drive.model.request.AddAnnouncementRequest;
import com.akagiyui.drive.model.response.AnnouncementDisplayResponse;
import com.akagiyui.drive.model.response.AnnouncementResponse;
import com.akagiyui.drive.service.AnnouncementService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
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

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private UserService userService;

    /**
     * 新增公告
     */
    @PostMapping({"", "/"})
    public Boolean addAnnouncement(@RequestBody @Validated AddAnnouncementRequest request) {
        // todo 权限校验
        Announcement announcement = request.toAnnouncement();
        announcement.setAuthor(userService.getUser());
        return announcementService.addAnnouncement(announcement);
    }

    /**
     * 获取公告列表
     */
    @GetMapping({"", "/"})
    public List<AnnouncementResponse> getAnnouncementList(@RequestParam(defaultValue = "false") Boolean all) {
        // todo 权限校验
        return AnnouncementResponse.fromAnnouncementList(announcementService.getAnnouncementList(all));
    }

    /**
     * 获取用于首页展示的公告列表
     */
    @GetMapping("/index")
    public List<AnnouncementDisplayResponse> getIndexAnnouncementList() {
        return AnnouncementDisplayResponse.fromAnnouncementList(announcementService.getAnnouncementDisplayList());
    }

}
