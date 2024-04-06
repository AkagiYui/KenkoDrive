package com.akagiyui.drive.service.impl;

import com.akagiyui.common.ResponseEnum;
import com.akagiyui.common.exception.CustomException;
import com.akagiyui.drive.entity.Announcement;
import com.akagiyui.drive.model.filter.AnnouncementFilter;
import com.akagiyui.drive.model.request.UpdateAnnouncementRequest;
import com.akagiyui.drive.repository.AnnouncementRepository;
import com.akagiyui.drive.service.AnnouncementService;
import jakarta.annotation.Resource;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 公告服务实现类
 * @author AkagiYui
 */
@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Resource
    private AnnouncementRepository announcementRepository;

    /**
     * 根据id查找公告或抛出异常
     *
     * @param id 公告id
     * @return 公告实体
     */
    private Announcement getAnnouncement(String id) {
        return announcementRepository.findById(id).orElseThrow(
            () -> new CustomException(ResponseEnum.NOT_FOUND)
        );
    }

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

    @Override
    public Page<Announcement> find(Integer index, Integer size, AnnouncementFilter filter) {
        Pageable pageable = PageRequest.of(index, size);

        // 条件查询
        Specification<Announcement> specification = (root, query, cb) -> {
            if (filter != null && StringUtils.hasText(filter.getExpression())) {
                String queryString = "%" + filter.getExpression() + "%";
                Predicate titlePredicate = cb.like(root.get("title"), queryString);
                Predicate contentPredicate = cb.like(root.get("content"), queryString);
                return cb.or(titlePredicate, contentPredicate);
            }
            return null;
        };

        return announcementRepository.findAll(specification, pageable);
    }

    @Override
    public void disable(String id, boolean disabled) {
        Announcement announcement = getAnnouncement(id);
        announcement.setEnabled(!disabled);
        announcementRepository.save(announcement);
    }

    @Override
    public void delete(String id) {
        Announcement announcement = getAnnouncement(id);
        announcementRepository.delete(announcement);
    }

    @Override
    public void update(String id, UpdateAnnouncementRequest request) {
        Announcement announcement = getAnnouncement(id);
        if (StringUtils.hasText(request.getTitle())) {
            announcement.setTitle(request.getTitle());
        }
        if (StringUtils.hasText(request.getContent())) {
            announcement.setContent(request.getContent());
        }
        announcementRepository.save(announcement);
    }
}
