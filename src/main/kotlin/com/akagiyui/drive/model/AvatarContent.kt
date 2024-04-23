package com.akagiyui.drive.model

/**
 * 头像内容
 *
 * @author AkagiYui
 */
data class AvatarContent(
    val content: ByteArray,
    val contentType: String,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvatarContent

        if (contentType != other.contentType) return false
        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = content.contentHashCode()
        result = 31 * result + contentType.hashCode()
        return result
    }

}
