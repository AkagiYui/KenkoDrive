package com.akagiyui.drive.controller;

import com.akagiyui.drive.component.permission.RequirePermission;
import com.akagiyui.drive.entity.Role;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.RoleFilter;
import com.akagiyui.drive.model.request.AddRoleRequest;
import com.akagiyui.drive.model.request.UpdateRoleRequest;
import com.akagiyui.drive.model.response.PageResponse;
import com.akagiyui.drive.model.response.PermissionResponse;
import com.akagiyui.drive.model.response.RoleInfoResponse;
import com.akagiyui.drive.service.RoleService;
import com.akagiyui.drive.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 角色 控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/role")
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;

    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    /**
     * 获取角色信息
     *
     * @return 角色信息 列表
     */
    @GetMapping({"", "/"})
    @RequirePermission(Permission.ROLE_VIEW)
    @Transactional
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
     * @return 角色id
     */
    @PostMapping({"", "/"})
    @RequirePermission(Permission.ROLE_ADD)
    public String addRole(@RequestBody AddRoleRequest role) {
        return roleService.addRole(role);
    }

    /**
     * 删除角色
     *
     * @param id 角色id
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.ROLE_DELETE)
    public void deleteRole(@PathVariable("id") String id) {
        roleService.deleteRole(id);
    }

    /**
     * 更新角色
     *
     * @param id   角色id
     * @param role 角色
     */
    @PutMapping("/{id}")
    @RequirePermission(Permission.ROLE_UPDATE)
    public void updateRole(@PathVariable("id") String id, @RequestBody UpdateRoleRequest role) {
        roleService.updateRole(id, role);
    }

    /**
     * 设置角色状态
     *
     * @param id       角色id
     * @param disabled 是否禁用
     */
    @PutMapping("/{id}/status")
    @RequirePermission(Permission.ROLE_UPDATE)
    public void updateStatus(@PathVariable("id") String id, @RequestParam(required = false) Boolean disabled) {
        if (disabled != null) {
            roleService.disable(id, disabled);
        }
    }

    /**
     * 获取所有权限
     *
     * @return 权限列表
     */
    @GetMapping("/permissions")
    @PreAuthorize("permitAll()")
    public List<PermissionResponse> getPermissions() {
        return PermissionResponse.fromPermissionList(roleService.getAllPermissions());
    }

    /**
     * 获取角色用户
     *
     * @param id 角色id
     * @return 用户id列表
     */
    @GetMapping("/{id}/users")
    @RequirePermission(Permission.USER_VIEW)
    public List<String> getUsers(@PathVariable("id") String id) {
        return roleService.getUsers(id).stream().map(User::getId).toList();
    }

    /**
     * 分配用户
     *
     * @param id      角色id
     * @param userIds 用户id列表
     */
    @PutMapping("/{id}/users")
    @RequirePermission(Permission.ROLE_ASSIGN)
    public void setUsers(@PathVariable("id") String id, @RequestBody Set<String> userIds) {
        userIds.forEach(userId -> userService.addRoles(userId, Set.of(id)));
    }

    /**
     * 移除用户
     *
     * @param id      角色id
     * @param userIds 用户id列表
     */
    @DeleteMapping("/{id}/users")
    @RequirePermission(Permission.ROLE_ASSIGN)
    public void removeUsers(@PathVariable("id") String id, @RequestBody List<String> userIds) {
        userIds.forEach(userId -> userService.removeRoles(userId, Set.of(id)));
    }
}
