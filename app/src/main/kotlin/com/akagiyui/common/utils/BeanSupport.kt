package com.akagiyui.common.utils

import io.minio.GetObjectResponse
import org.springframework.http.ResponseEntity
import kotlin.reflect.KProperty0

/**
 * 属性是否已初始化
 * @author AkagiYui
 */
fun KProperty0<*>.isLateinitInitialized(): Boolean {
    return if (this.isLateinit) {
        try {
            this.get()
            true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    } else {
        true
    }
}

/**
 * Extension toString for Minio GetObjectResponse
 */
fun GetObjectResponse.toStr(): String {
    return "GetObjectResponse(bucket=${bucket()}, object=${`object`()})"
}

/**
 * Extension toString for ResponseEntity
 */
fun ResponseEntity<*>.toStr(): String {
    return "ResponseEntity<${statusCode.value()}>[${body.toString()}]"
}

/**
 * Extension toString for ByteArray
 */
fun ByteArray.toStr(): String {
    return "ByteArray[$size]"
}
