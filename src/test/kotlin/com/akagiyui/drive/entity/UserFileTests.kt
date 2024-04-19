package com.akagiyui.drive.entity

import com.akagiyui.drive.repository.FileInfoRepository
import com.akagiyui.drive.repository.UserFileRepository
import com.akagiyui.drive.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

/**
 * 用户文件关联 测试类
 *
 * @author AkagiYui
 */
@SpringBootTest
class UserFileTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val fileInfoRepository: FileInfoRepository,
    private val userFileRepository: UserFileRepository,
) {

    @Test
    @Transactional
    fun addUserFile() {
        // 新增用户
        val user = User()
            .setUsername("test")
            .setPassword("test")
            .setNickname("测试用户")
            .setEmail("test@test.com")
        userRepository.save(user)
        println("新增用户id: ${user.id}")

        // 新增文件
        val fileInfo = FileInfo()
            .setSize(123L)
            .setHash("test hash")
            .setName("test name")
            .setType("test type")
            .setStorageKey("test storage key")
        fileInfoRepository.save(fileInfo)
        println("新增文件id: ${fileInfo.id}")

        // 新增用户文件关联
        val userFile = UserFile()
            .setUser(user)
            .setFileInfo(fileInfo)
            .setName("test name")
        userFileRepository.save(userFile)

        // 查询所有用户文件关联
        val all = userFileRepository.findAll()
        Assertions.assertEquals(1, all.size)
    }


}
