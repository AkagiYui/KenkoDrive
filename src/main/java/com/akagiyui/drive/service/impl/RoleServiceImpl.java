package com.akagiyui.drive.service.impl;

import com.akagiyui.drive.model.response.RoleInfoResponse;
import com.akagiyui.drive.repository.RoleRepository;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色服务实现类
 *
 * @author kenko
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private RoleRepository roleRepository;

    @Override
    public List<RoleInfoResponse> getAllRoles() {
        return RoleInfoResponse.fromRoleList(roleRepository.findAll());
    }
}
