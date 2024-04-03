package com.akagiyui.drive.entity;

import com.akagiyui.drive.repository.FileInfoRepository;
import com.akagiyui.drive.repository.UserFileRepository;
import com.akagiyui.drive.repository.UserRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 用户文件关联 测试类
 *
 * @author AkagiYui
 */
@SpringBootTest
class UserFileTest {

    @Resource
    private UserRepository userRepository;

    @Resource
    private FileInfoRepository fileInfoRepository;

    @Resource
    private UserFileRepository userFileRepository;

    @Test
    @Transactional
    void addUserFile() {
        // 新增用户
        User user = new User()
            .setUsername("test")
            .setPassword("test")
            .setNickname("测试用户")
            .setEmail("test@test.com");
        userRepository.save(user);
        System.out.printf("新增用户id: %s\n", user.getId());

        // 新增文件
        FileInfo fileInfo = new FileInfo()
            .setSize(123L)
            .setHash("test hash")
            .setName("test name")
            .setType("test type")
            .setStorageKey("test storage key");
        fileInfoRepository.save(fileInfo);
        System.out.printf("新增文件id: %s\n", fileInfo.getId());

        // 新增用户文件关联
        UserFile userFile = new UserFile()
            .setUser(user)
            .setFileInfo(fileInfo)
            .setName("test name");
        userFileRepository.save(userFile);

        // 查询所有用户文件关联
        List<UserFile> all = userFileRepository.findAll();
        assertEquals(1, all.size());
    }

}
