package com.akagiyui.drive.config

import com.akagiyui.common.ResponseEnum
import com.akagiyui.common.ResponseResult
import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.exception.CustomException
import com.akagiyui.common.exception.TooManyRequestsException
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler
import org.springframework.security.web.firewall.RequestRejectedException
import org.springframework.security.web.firewall.RequestRejectedHandler
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.multipart.MultipartException
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.IOException

/**
 * 全局异常处理器
 *
 * @author AkagiYui
 */
@RestControllerAdvice
class CustomExceptionHandler {
    private val log by LoggerDelegate()

    /**
     * 404 API未找到
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(
        NoHandlerFoundException::class, // 无路由
        HttpRequestMethodNotSupportedException::class, // 请求方法不支持
    )
    fun noRouteException(ignored: Exception): ResponseResult<Any> {
        return ResponseResult.response(ResponseEnum.NOT_FOUND)
    }

    /**
     * 客户端中断连接，常见于文件下载
     */
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(IOException::class)
    fun requestRejectedException(ignored: IOException): ResponseEntity<Unit> {
        return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).build()
    }

    /**
     * 400 请求体错误
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        HttpMessageNotReadableException::class, // 请求体为空
        MissingServletRequestParameterException::class, // 缺少请求参数
        MissingRequestHeaderException::class, // 缺少请求头
        MaxUploadSizeExceededException::class, // 文件过大
        MethodArgumentNotValidException::class, // 参数校验异常
        MultipartException::class, // 文件上传异常
    )
    fun badRequestException(e: Exception): ResponseResult<Any> {
        // 目前可预见的是 JSON 解析错误
        e.cause?.let {
            return when (it) {
                is JsonParseException -> ResponseResult.response(ResponseEnum.BAD_REQUEST, "JSON parse error")
                else -> {
                    log.error("Bad request", it)
                    ResponseResult.response(ResponseEnum.BAD_REQUEST, it.message)
                }
            }
        }

        // 无请求体 错误
        if (e.message?.startsWith("Required request body is missing") == true) {
            return ResponseResult.response(ResponseEnum.BAD_REQUEST, "Request body is missing")
        }
        // 参数校验异常
        if (e is MethodArgumentNotValidException) {
            e.bindingResult.fieldError?.let {
                var message = it.defaultMessage
                if (message != null && message.startsWith("{") && message.endsWith("}")) {
                    message = message.substring(1, message.length - 1)
                }
                return ResponseResult.response(ResponseEnum.BAD_REQUEST, message)
            }
            return ResponseResult.response(ResponseEnum.BAD_REQUEST)
        }
        return ResponseResult.response(ResponseEnum.BAD_REQUEST, e.message)
    }

    /**
     * 429 请求过快
     */
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(TooManyRequestsException::class)
    fun tooManyRequestsException(ignored: TooManyRequestsException): ResponseResult<Any> {
        return ResponseResult.response(ResponseEnum.TOO_MANY_REQUESTS)
    }

    /**
     * 200/403 自定义异常
     */
    @ExceptionHandler(CustomException::class)
    fun customException(e: CustomException): ResponseEntity<Any> {
        val responseBody = ResponseResult.response<Any?>(e.getStatus())
        return if (e.getStatus() == ResponseEnum.UNAUTHORIZED) {
            ResponseEntity(responseBody, HttpStatus.FORBIDDEN) // 403 Forbidden
        } else {
            ResponseEntity(responseBody, HttpStatus.OK) // 200 OK
        }
    }

    /**
     * 500 其他异常
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(
        Exception::class,
        JsonMappingException::class, // ObjectMapper 解析错误，可能由 lateinit 属性导致
    )
    fun unknownException(e: Exception): ResponseResult<Any> {
        log.error("Unknown exception", e)
        return ResponseResult.response(ResponseEnum.INTERNAL_ERROR)
    }

    /**
     * 处理 Spring Security 请求拒绝异常
     *
     * @return RequestRejectedHandler
     */
    @Bean
    fun requestRejectedHandler(): RequestRejectedHandler {
        return object : HttpStatusRequestRejectedHandler() {
            override fun handle(
                request: HttpServletRequest,
                response: HttpServletResponse,
                requestRejectedException: RequestRejectedException,
            ) {
                ResponseResult.writeResponse(response, HttpStatus.BAD_REQUEST, ResponseEnum.BAD_REQUEST)
            }
        }
    }

    /**
     * 处理 Spring Security 访问拒绝异常
     *
     * @return AccessDeniedHandler
     */
    @Bean
    fun accessDeniedHandler(): AccessDeniedHandler {
        return AccessDeniedHandler { _, response, _ ->
            ResponseResult.writeResponse(response, HttpStatus.FORBIDDEN, ResponseEnum.FORBIDDEN)
        }
    }

    /**
     * 处理 Spring Security 认证异常
     *
     * @return AuthenticationEntryPoint
     */
    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint =
        AuthenticationEntryPoint { _, response, _ ->
            ResponseResult.writeResponse(response, HttpStatus.UNAUTHORIZED, ResponseEnum.UNAUTHORIZED)
        }
}
