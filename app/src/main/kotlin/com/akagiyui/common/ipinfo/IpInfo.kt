package com.akagiyui.common.ipinfo

/**
 * Ip信息
 * @author AkagiYui
 */
data class IpInfo(
    /**
     * IP地址
     */
    val ipAddress: String,
    /**
     * 大洲
     */
    val continent: String,
    /**
     * 国家
     */
    val country: String,
    /**
     * 省份
     */
    val province: String,
    /**
     * 城市
     */
    val city: String,
)
