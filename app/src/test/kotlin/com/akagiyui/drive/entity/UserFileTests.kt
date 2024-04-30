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
        val user = User().apply {
            username = "test"
            password = "test"
            nickname = "测试用户"
            email = "test@test.com"
        }
        userRepository.save(user)
        println("新增用户id: ${user.id}")

        // 新增文件
        val fileInfo = FileInfo().apply {
            size = 123L
            hash = "test hash"
            name = "test name"
            type = "test type"
            storageKey = "test storage key"
        }
        fileInfoRepository.save(fileInfo)
        println("新增文件id: ${fileInfo.id}")

        // 新增用户文件关联
        val userFile = UserFile().apply {
            this.user = user
            this.fileInfo = fileInfo
            name = "test name"
        }
        userFileRepository.save(userFile)

        // 查询所有用户文件关联
        val all = userFileRepository.findAll()
        Assertions.assertEquals(1, all.size)
    }


}
