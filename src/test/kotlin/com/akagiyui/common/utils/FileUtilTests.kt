package com.akagiyui.common.utils

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

/**
 * 文件工具测试类
 * @author AkagiYui
 */
class FileUtilTests {

    @Test
    fun getResourceFile() {
        val file = FileUtil.getResourceFile("test.txt")
        Assertions.assertTrue(file.exists())
    }

    @Test
    fun getResourcePath() {
        val path = FileUtil.getResourcePath("test.txt")
        Assertions.assertTrue(path.isNotEmpty())
    }

    @Test
    fun readToString() {
        val classLoader = FileUtilTests::class.java.classLoader
        val file = File(classLoader.getResource("test.txt")!!.file)
        val content = file.readToString()
        Assertions.assertEquals("abc", content)
    }
}
