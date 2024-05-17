package com.akagiyui.drive.service

/**
 * 系统服务接口
 * @author AkagiYui
 */

interface SystemService {

    /**
     * 添加实时信息回调
     */
    fun addRealTimeInfoCallback(callback: (Map<String, Any>) -> Unit)

    /**
     * 删除实时信息回调
     */
    fun removeRealTimeInfoCallback(callback: (Map<String, Any>) -> Unit)

    /**
     * 获取历史内存信息
     */
    fun getMemoryInfoHistory(): List<Map<String, Number>>

}
