package com.akagiyui.common.utils

import java.io.File
import java.io.InputStream


/**
 * 文件工具类
 * File类为Java类，没有伴生对象，无法扩展方法，这里使用工具类
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
     * 获取 resources 目录下的文件流（可在Jar包中使用）
     *
     * @param path 文件路径
     * @return 文件流
     */
    fun getResourceFileStream(path: String): InputStream {
        val classLoader = FileUtil::class.java.classLoader
        return classLoader.getResourceAsStream(path) ?: throw NullPointerException()
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

}
