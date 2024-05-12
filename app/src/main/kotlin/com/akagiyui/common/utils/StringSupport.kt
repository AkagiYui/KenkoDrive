package com.akagiyui.common.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * 截断字符串
 *
 * @param length 截断长度
 * @return 截断后的字符串
 */
fun String.ellipsis(length: Int): String {
    if (this.length <= length) return this
    return if (length >= 3) {
        this.take(length - 3) + "..."
    } else {
        this.take(length)
    }
}

/**
 * 压缩包名
 *
 * @param allowedLength 允许的包名长度
 * @return 压缩后的包名
 */
fun String.compressPackageName(allowedLength: Int? = null): String {
    var parts = this.split('.') // 分割字符串以获取包名和类名
    if (parts.size <= 1) return this // 如果没有包名或者只有一个单词就直接返回原字符串
    val className = parts.last() // 获取类名
    parts = parts.dropLast(1).toMutableList() // 去掉最后一个元素，即类名

    if (allowedLength != null) {
        // 从前往后逐个包名部分压缩，直到总长度小于等于允许的长度
        var currentLength = this.length
        for (i in parts.indices) {
            currentLength -= parts[i].length + 1 // 减去当前包名部分的长度和一个点的长度
            parts[i] = parts[i].first().toString() // 只保留包名部分的第一个字符
            if (currentLength <= allowedLength) break // 如果总长度小于等于允许的长度就退出循环
        }
    } else {
        for (i in parts.indices) {
            parts[i] = parts[i].first().toString() // 只保留包名部分的第一个字符
        }
    }

    val compressedPackage = parts.joinToString(".") // 重新拼接压缩后的包名
    return "$compressedPackage.$className" // 返回压缩后的包名加上类名
}

/**
 * 判断字符串是否为空
 *
 * @return 是否为空
 */
@OptIn(ExperimentalContracts::class)
fun String?.hasText(): Boolean {
    // 契约：如果返回true，则说明this为String
    contract {
        returns(true) implies (this@hasText is String)
    }
    return !this.isNullOrBlank()
}

/**
 * 转换为驼峰命名
 * 例如：user_name -> userName
 *
 * @return 驼峰命名
 */
fun String.toCamelCase(): String {
    val list = this.split("_").toMutableList()
    list[0] = list[0].lowercase()
    for (i in 1 until list.size) {
        list[i] = list[i].lowercase().replaceFirstChar { it.uppercase() }
    }
    return list.joinToString("")
}

/**
 * 转换为下划线命名
 * 例如：userName -> user_name
 *
 * @param upperCase 是否大写
 * @return 下划线命名
 */
fun String.toUnderscoreCase(upperCase: Boolean = false): String {
    var list = this.split("(?=[A-Z])".toRegex()).toMutableList()
    if (list[0].isBlank()) {
        list = list.drop(1).toMutableList()
    }
    // 遍历除了第一个元素之外的所有元素，将其转换为小写
    for (i in list.indices) {
        list[i] = list[i].lowercase()
    }
    val lowerSnakeString = list.joinToString("_")
    return if (upperCase) {
        lowerSnakeString.uppercase()
    } else {
        lowerSnakeString
    }
}

val invalidCharsRegex by lazy { "[\\\\/:*?\"<>|]".toRegex() }

/**
 * 转换为文件系统安全的文件名
 * @return 文件系统安全的文件名
 */
fun String.toSafeFileName(): String {
    return this.replace(invalidCharsRegex, "_")
}

val String.Companion.BASE_NUMBER: String
    get() = "0123456789"


/**
 * 生成随机字符串
 *
 * @param base   基础字符集
 * @param length 长度
 * @return 随机字符串
 */
fun String.Companion.random(base: String, length: Int): String {
    val random = java.util.Random()
    val sb = StringBuilder()
    for (i in 0 until length) {
        val number = random.nextInt(base.length)
        sb.append(base[number])
    }
    return sb.toString()
}
