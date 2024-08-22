package com.akagiyui.common.ipinfo

/**
 * Ip信息获取器
 * @author AkagiYui
 */
interface IpInfoFetcher {
    /**
     * 获取IP信息
     */
    fun fetch(ip: String): IpInfo
}
