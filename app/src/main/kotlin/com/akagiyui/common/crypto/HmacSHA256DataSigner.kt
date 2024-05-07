package com.akagiyui.common.crypto

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * HmacSHA256 数据签名器
 * @author AkagiYui
 */

class HmacSHA256DataSigner(val key: String) : DataSigner {
    private val signer = Mac.getInstance("HmacSHA256").apply {
        init(SecretKeySpec(key.toByteArray(), "HmacSHA256"))
    }

    override fun sign(data: String): String {
        return signer.doFinal(data.toByteArray()).joinToString("") { "%02x".format(it) }
    }
}
