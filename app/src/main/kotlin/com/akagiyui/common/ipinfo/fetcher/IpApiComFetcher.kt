package com.akagiyui.common.ipinfo.fetcher

import com.akagiyui.common.ipinfo.IpInfo
import com.akagiyui.common.ipinfo.IpInfoFetcher
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

/**
 * ip-api.com IP信息获取器
 * @author AkagiYui
 */
class IpApiComFetcher : IpInfoFetcher {
    private val httpClient: HttpClient = HttpClient.newHttpClient()
    private val objectMapper = ObjectMapper()

    override fun fetch(ip: String): IpInfo {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://ip-api.com/json/$ip?fields=66846719&lang=zh-CN"))
            .GET()
            .build()
        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())

        // to json
        val jsonMap = objectMapper.readValue(response.body(), Map::class.java)
        return IpInfo(
            ipAddress = jsonMap["query"] as String,
            continent = jsonMap["continent"] as String,
            country = jsonMap["country"] as String,
            province = jsonMap["regionName"] as String,
            city = jsonMap["city"] as String,
        )

    }

}
