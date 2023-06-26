package com.akagiyui.drive.controller;

import com.akagiyui.drive.entity.User;
import com.akagiyui.drive.entity.filter.UserFilter;
import com.akagiyui.drive.entity.request.AddUserRequest;
import com.akagiyui.drive.entity.response.PageResponse;
import com.akagiyui.drive.entity.response.UserInfoPage;
import com.akagiyui.drive.entity.response.UserInfoResponse;
import com.akagiyui.drive.service.UserService;
import jakarta.annotation.Resource;
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
    @GetMapping("")
    public PageResponse<UserInfoResponse> getPage(
            @RequestParam Integer index,
            @RequestParam Integer size,
            @ModelAttribute UserFilter filter
    ) {
        Page<User> userPage = userService.find(index, size, filter);
        List<User> userList = userPage.getContent();
        List<UserInfoResponse> studentResponseList = userList.stream()
                .map(UserInfoResponse::fromUser)
                .collect(Collectors.toList());

        return new UserInfoPage()
                .setPage(index)
                .setSize(size)
                .setPageCount(userPage.getTotalPages())
                .setTotal(userPage.getTotalElements())
                .setList(studentResponseList);
    }
}
