package com.akagiyui.drive.component.ip

import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * 客户端IP参数解析器
 * @author AkagiYui
 */
class ClientIpArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(ClientIp::class.java)

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): String {
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        return getClientIpAddress(request)
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
        val headerNames = arrayOf(
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
        )

        return headerNames
            .map { request.getHeader(it) }
            .firstOrNull { !it.isNullOrEmpty() && it.lowercase() != "unknown" }
            ?.split(",")
            ?.first()
            ?.trim()
            ?: request.remoteAddr
    }
}
