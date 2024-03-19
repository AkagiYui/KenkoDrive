package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.entity.Announcement;
import com.akagiyui.drive.repository.AnnouncementRepository;
import com.akagiyui.drive.service.AnnouncementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 公告服务实现类
 * @author AkagiYui
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementRepository announcementRepository;

    @Override
    public void addAnnouncement(Announcement announcement) {
        announcementRepository.save(announcement);
    }

    @Override
    public List<Announcement> getAnnouncementList(boolean all) {
        if (all) {
            return announcementRepository.findAll();
        } else {
            return announcementRepository.findAnnouncementsByEnabledIsTrue();
        }
    }

    @Override
    public List<Announcement> getAnnouncementDisplayList() {
        return announcementRepository.findAnnouncementsByEnabledIsTrueOrderByUpdateTimeDesc();
    }
}
