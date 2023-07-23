package com.akagiyui.drive.controller;

import com.akagiyui.drive.component.limiter.Limit;
import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.model.filter.UserFilter;
import com.akagiyui.drive.model.request.AddUserRequest;
import com.akagiyui.drive.model.request.EmailVerifyCodeRequest;
import com.akagiyui.drive.model.request.RegisterConfirmRequest;
import com.akagiyui.drive.model.response.PageResponse;
import com.akagiyui.drive.model.response.UserInfoResponse;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户 API
 * @author AkagiYui
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 根据用户id查找用户
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    User findById(@PathVariable("id") String id) {
        return userService.findUserById(id);
    }

    /**
     * 新增用户
     * @param user 用户
     * @return 是否成功
     */
    @PostMapping
    boolean add(@Validated @RequestBody AddUserRequest user) {
        return userService.addUser(user);
    }

    /**
     * 分页查询用户
     *
     * @param index 页码
     * @param size 每页大小
     * @return 用户分页响应
     */
    @GetMapping({"", "/"})
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
    public Boolean delete(@PathVariable String id) {
        return userService.delete(id);
    }

    /**
     * 获取邮件验证码
     * @param verifyRequest 预注册请求体
     * @return 是否成功
     */
    @PostMapping("/register/email")
    @Limit(key = "getVerifyCode", permitsPerSecond = 1, timeout = 1)
    public boolean getEmailVerifyCode(@RequestBody @Valid EmailVerifyCodeRequest verifyRequest) {
        return userService.sendEmailVerifyCode(verifyRequest);
    }

    /**
     * 确认注册
     * @param registerConfirmRequest 注册请求体
     * @return 是否成功
     */
    @PostMapping("/register")
    public boolean confirmRegister(@RequestBody @Valid RegisterConfirmRequest registerConfirmRequest) {
        return userService.confirmRegister(registerConfirmRequest);
    }
}
