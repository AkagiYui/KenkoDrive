package com.akagiyui.common.utils

import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * 系统信息工具测试
 * @author AkagiYui
 */

class SystemInformationUtilTests {

    @Test
    fun getCpuUsageInformation() {
        val information = SystemInformationUtil.getCpuUsageInformation()
        println(information)
        println(information.usageRate)
    }

    @Test
    fun getJvmInformation() {
        val information = SystemInformationUtil.getJvmInformation()
        println(information)
        println(information.usedMemory)
    }

    @Test
    fun getMemoryInformation() {
        val information = SystemInformationUtil.getMemoryInformation(SizeEnum.GB)
        println(information)
        println(information.used)
    }

    @Test
    fun getSystemInformation() {
        val information = SystemInformationUtil.getSystemInformation()
        println(information)
    }

    @Test
    fun getNetworkTraffic() {
        val information = SystemInformationUtil.getNetworkTraffic()
        println(information)
        TimeUnit.SECONDS.sleep(2)
        val information2 = SystemInformationUtil.getNetworkTraffic()
        println(information2)
    }

    @Test
    fun getDiskTraffic() {
        val information = SystemInformationUtil.getDiskTraffic()
        println(information)
        TimeUnit.SECONDS.sleep(1)
        val information2 = SystemInformationUtil.getDiskTraffic()
        println(information2)
        information2.forEach { (t, u) ->
            val (r, w) = u
            val (r2, w2) = information[t]!!
            println("$t Read: ${r - r2}B/s, Write: ${w - w2}B/s")
        }
    }

    @Test
    fun getHardwareInformation() {
        val information = SystemInformationUtil.getHardwareInformation()
        println(information)
    }
}
