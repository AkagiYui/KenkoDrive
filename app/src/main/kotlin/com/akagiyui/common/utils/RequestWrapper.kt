package com.akagiyui.common.utils

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import java.util.*

/**
 * 自定义请求包装器，允许修改请求参数
 * @author AkagiYui
 */
class RequestWrapper(val request: HttpServletRequest) : HttpServletRequestWrapper(request) {

    private val parameterMap: MutableMap<String, Array<String>> = HashMap(request.parameterMap)

    override fun getParameter(name: String): String {
        return parameterMap[name]?.get(0) ?: ""
    }

    override fun getParameterMap(): Map<String, Array<String>> {
        return parameterMap
    }

    override fun getParameterNames(): Enumeration<String> {
        return Collections.enumeration(parameterMap.keys)
    }

    override fun getParameterValues(name: String): Array<String>? {
        return parameterMap[name]
    }

    /**
     * 设置请求参数
     * @param name 参数名
     * @param value 参数值
     */
    fun setParameter(name: String, value: String) {
        parameterMap[name] = arrayOf(value)
    }
}
