package com.akagiyui.drive.controller;

import com.akagiyui.common.limiter.Limit;
import com.akagiyui.drive.component.permission.RequirePermission;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.Permission;
import com.akagiyui.drive.model.filter.UserFilter;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest;
import com.akagiyui.drive.model.request.RegisterConfirmRequest;
import com.akagiyui.drive.model.request.UpdateUserInfoRequest;
import com.akagiyui.drive.model.response.PageResponse;
import com.akagiyui.drive.model.response.UserInfoResponse;
import com.akagiyui.drive.service.AvatarService;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 用户 API
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private AvatarService avatarService;

    /**
     * 根据用户id查找用户
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    @RequirePermission(Permission.USER_VIEW)
    User findById(@PathVariable("id") String id) {
        return userService.findUserById(id);
    }

    /**
     * 新增用户
     *
     * @param user 用户
     * @return 是否成功
     */
    @PostMapping
    @RequirePermission(Permission.USER_ADD)
    boolean add(@Validated @RequestBody AddUserRequest user) {
        return userService.addUser(user);
    }

    /**
     * 分页查询用户
     *
     * @param index 页码
     * @param size  每页大小
     * @return 用户分页响应
     */
    @GetMapping({"", "/"})
    @RequirePermission(Permission.USER_VIEW)
    public PageResponse<UserInfoResponse> getPage(
            @RequestParam(defaultValue = "0") Integer index,
            @RequestParam(defaultValue = "10") Integer size,
            @ModelAttribute UserFilter filter
    ) {
        Page<User> userPage = userService.find(index, size, filter);
        List<User> userList = userPage.getContent();
        List<UserInfoResponse> studentResponseList = userList.stream()
                .map(UserInfoResponse::fromUser)
                .collect(Collectors.toList());

        return new PageResponse<UserInfoResponse>()
                .setPage(index)
                .setSize(size)
                .setPageCount(userPage.getTotalPages())
                .setTotal(userPage.getTotalElements())
                .setList(studentResponseList);
    }

    /**
     * 根据 ID 删除用户
     *
     * @param id 用户 ID
     */
    @DeleteMapping("/{id}")
    @RequirePermission(Permission.USER_DELETE)
    public Boolean delete(@PathVariable String id) {
        return userService.delete(id);
    }

    /**
     * 启用/禁用用户
     *
     * @param id       用户 ID
     * @param disabled 是否禁用
     */
    @PutMapping("/{id}/disable")
    @RequirePermission(Permission.USER_UPDATE)
    public Boolean disable(@PathVariable String id, @RequestParam Boolean disabled) {
        return userService.disable(id, disabled);
    }

    /**
     * 获取邮件验证码
     *
     * @param verifyRequest 预注册请求体
     * @return 是否成功
     */
    @PostMapping("/register/email")
    @PreAuthorize("isAnonymous()")
    @Limit(key = "getVerifyCode", permitsPerSecond = 1, timeout = 1)
    public boolean getEmailVerifyCode(@RequestBody @Valid EmailVerifyCodeRequest verifyRequest) {
        return userService.sendEmailVerifyCode(verifyRequest);
    }

    /**
     * 确认注册
     *
     * @param registerConfirmRequest 注册请求体
     * @return 是否成功
     */
    @PostMapping("/register")
    @PreAuthorize("isAnonymous()")
    public boolean confirmRegister(@RequestBody @Valid RegisterConfirmRequest registerConfirmRequest) {
        return userService.confirmRegister(registerConfirmRequest);
    }

    /**
     * 获取当前用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public UserInfoResponse getUserInfo() {
        return UserInfoResponse.fromUser(userService.getUser());
    }

    /**
     * 更新当前用户信息
     */
    @PutMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public boolean updateUserInfo(@RequestBody @Valid UpdateUserInfoRequest userInfo) {
        return userService.updateInfo(userInfo);
    }

    /**
     * 上传头像
     *
     * @param avatar 头像文件
     * @return 是否成功
     */
    @PostMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public boolean updateAvatar(@RequestParam("avatar") MultipartFile avatar) {
        return avatarService.saveAvatar(avatar);
    }

    /**
     * 获取头像
     *
     * @return 头像
     */
    @GetMapping("/avatar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<byte[]> getAvatar() {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.valueOf("image/" + AvatarService.IMAGE_FORMAT));

        return ResponseEntity
                .ok()
                .headers(header)
                .body(avatarService.getAvatar());
    }

    /**
     * 获取当前用户权限列表
     */
    @GetMapping("/permission")
    @PreAuthorize("isAuthenticated()")
    public Set<String> getPermission() {
        return userService.getPermission();
    }

    /**
     * 获取当前用户角色列表
     */
    @GetMapping("/role")
    @PreAuthorize("isAuthenticated()")
    public Set<String> getRole() {
        return userService.getRole();
    }
}
