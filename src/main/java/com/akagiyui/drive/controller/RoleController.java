package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.model.filter.RoleFilter;
import com.akagiyui.drive.model.request.AddRoleRequest;
import com.akagiyui.drive.model.response.PageResponse;
import com.akagiyui.drive.model.response.PermissionResponse;
import com.akagiyui.drive.model.response.RoleInfoResponse;
import com.akagiyui.drive.service.RoleService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    /**
     * 获取角色信息
     *
     * @return 角色信息 列表
     */
    @GetMapping({"", "/"})
    @PreAuthorize("permitAll()")
    public PageResponse<RoleInfoResponse> getPage(
        @RequestParam(defaultValue = "0") Integer index,
        @RequestParam(defaultValue = "10") Integer size,
        @ModelAttribute RoleFilter filter
    ) {
        Page<Role> rolePage = roleService.find(index, size, filter);
        List<Role> roleList = rolePage.getContent();
        List<RoleInfoResponse> roleResponseList = RoleInfoResponse.fromRoleList(roleList);

        return new PageResponse<RoleInfoResponse>()
            .setPage(index)
            .setSize(size)
            .setPageCount(rolePage.getTotalPages())
            .setTotal(rolePage.getTotalElements())
            .setList(roleResponseList);
    }

    /**
     * 添加角色
     *
     * @param role 角色
     * @return 是否成功
     */
    @PostMapping({"", "/"})
    // todo 权限校验
    public boolean addRole(@RequestBody AddRoleRequest role) {
        return roleService.addRole(role);
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @RequestMapping("/permissions")
    @PreAuthorize("permitAll()")
    public List<PermissionResponse> getPermissions() {
        return PermissionResponse.fromPermissionList(roleService.getAllPermissions());
    }
}
