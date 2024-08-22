package com.akagiyui.drive.service.impl

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.ipinfo.IpInfoFetcher
import com.akagiyui.common.ipinfo.fetcher.IpApiComFetcher
import com.akagiyui.common.utils.isIpv4
import com.akagiyui.common.utils.isIpv6
import com.akagiyui.common.utils.isLocalIpv4Address
import com.akagiyui.common.utils.isLocalIpv6Address
import com.akagiyui.drive.component.RedisCache
import com.akagiyui.drive.service.IpRegionService
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class IpRegionServiceImpl(
    private val redisCache: RedisCache,
) : IpRegionService {
    private val log by LoggerDelegate()
    private val ipInfoFetcher: IpInfoFetcher = IpApiComFetcher()

    override fun getRegion(ip: String): String {
        if (!ip.isIpv4() && !ip.isIpv6()) {
            log.warn("Invalid IP address: $ip")
            return "未知"
        }
        if (ip.isLocalIpv4Address() || ip.isLocalIpv6Address()) {
            log.warn("Local IP address: $ip")
            return "未知"
        }

        val key = "ip-region:$ip"
        val cachedRegion: String? = redisCache[key]
        if (cachedRegion != null) {
            return cachedRegion
        }

        val ipInfo = ipInfoFetcher.fetch(ip)
        val regionString = "${ipInfo.country}-${ipInfo.province}-${ipInfo.city}"
        redisCache[key, 2, TimeUnit.DAYS] = regionString
        return regionString
    }

}
