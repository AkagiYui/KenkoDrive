package com.akagiyui.common.utils

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.file.Files


/**
 * 文件工具类
 *
 * @author AkagiYui
 */
object FileUtil {

    /**
     * 获取 resources 目录下的文件
     *
     * @param path 文件路径
     * @return File
     */
    fun getResourceFile(path: String): File {
        val classLoader = FileUtil::class.java.classLoader
        return File(classLoader.getResource(path)?.file ?: throw NullPointerException())
    }

    /**
     * 获取 resources 目录下的文件绝对路径
     *
     * @param path 文件路径
     * @return 文件绝对路径
     */
    fun getResourcePath(path: String): String {
        val classLoader = Thread.currentThread().contextClassLoader
        val resourcePath = classLoader.getResource("")?.toString() ?: throw NullPointerException()
        return resourcePath.replace("file:/", "") + path
    }

    /**
     * 获取 webapp 目录下的文件
     *
     * @param path 文件路径
     * @return 完整路径
     */
    fun getStaticResourcePath(path: String): String {
        val classLoader = Thread.currentThread().contextClassLoader
        val resourcePath = classLoader.getResource("")?.toString() ?: throw NullPointerException()
        val cleanPath = resourcePath.replace("file:/", "")
        return cleanPath.substring(0, cleanPath.indexOf("/WEB-INF")) + path
    }

    /**
     * 读取文件内容为字符串
     *
     * @param file    文件
     * @param charset 字符集
     * @return 文件内容
     * @throws IOException 读取文件异常
     */
    @Throws(IOException::class)
    fun readFileToString(file: File, charset: String): String {
        BufferedReader(InputStreamReader(Files.newInputStream(file.toPath()), charset)).use { reader ->
            return reader.lineSequence().joinToString(System.lineSeparator())
        }
    }

    /**
     * 读取文件内容为字符串，默认字符集为 utf-8
     *
     * @param file 文件
     * @return 文件内容
     * @throws IOException 读取文件异常
     */
    @Throws(IOException::class)
    fun readFileToString(file: File): String {
        return readFileToString(file, "utf-8")
    }
}
