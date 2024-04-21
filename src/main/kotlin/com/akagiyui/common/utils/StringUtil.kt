package com.akagiyui.common.utils

/**
 * 截断字符串
 *
 * @author AkagiYui
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
