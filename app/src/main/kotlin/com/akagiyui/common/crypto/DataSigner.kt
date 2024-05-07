package com.akagiyui.common.crypto

/**
 * 数据签名接口
 * @author AkagiYui
 */
fun interface DataSigner {
    /**
     * 签名
     *
     * @param data 数据
     * @return 签名
     */
    fun sign(data: String): String
}
