package com.akagiyui.drive.service

/**
 * IP 地址区域服务
 * @author AkagiYui
 */
interface IpRegionService {
    /**
     * 获取IP地址所在区域
     * @param ip IP地址
     * @return 区域
     */
    fun getRegion(ip: String): String
}
