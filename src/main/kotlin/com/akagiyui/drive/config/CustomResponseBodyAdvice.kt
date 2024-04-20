package com.akagiyui.drive.config

import com.akagiyui.common.ResponseResult
import org.springframework.core.MethodParameter
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice

/**
 * 包装类处理
 *
 * @author AkagiYui
 */
@RestControllerAdvice
class CustomResponseBodyAdvice : ResponseBodyAdvice<Any>, WebMvcConfigurer {

    companion object {
        // 忽略的类
        private val IGNORE_CLASSES = listOf(
            ResponseEntity::class.java, // 文件
            ByteArray::class.java, // 二进制数据
            ResponseResult::class.java, // 已经包装过的数据
        )
    }

    /**
     * 响应数据包装
     *
     * @param body 返回的数据
     * @param returnType 返回的数据类型
     * @param selectedContentType 返回的 Content-Type
     * @param selectedConverterType 返回的数据类型
     * @param request 请求
     * @param response 响应
     * @return 包装后的数据
     */
    override fun beforeBodyWrite(
        body: Any?,
        returnType: MethodParameter,
        selectedContentType: MediaType,
        selectedConverterType: Class<out HttpMessageConverter<*>>,
        request: ServerHttpRequest,
        response: ServerHttpResponse,
    ): Any? {
        // 包装返回的数据
        return ResponseResult.success(body)
    }

    /**
     * 判断是否需要执行 beforeBodyWrite 方法
     *
     * @param returnType 返回的数据类型
     * @param converterType 返回的数据类型
     * @return 是否需要执行 beforeBodyWrite 方法
     */
    override fun supports(
        returnType: MethodParameter,
        converterType: Class<out HttpMessageConverter<*>>,
    ): Boolean {
        val parameterType = returnType.parameterType
        return !IGNORE_CLASSES.contains(parameterType)
    }

    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>>) {
        // 把String类型的转换器去掉，不使用String类型的转换器，防止过早转换发生异常
        converters.removeIf { converter -> converter is StringHttpMessageConverter }
    }
}
