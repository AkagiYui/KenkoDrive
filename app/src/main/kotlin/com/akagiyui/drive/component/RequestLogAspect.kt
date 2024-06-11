package com.akagiyui.drive.component

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.utils.compressPackageName
import com.akagiyui.common.utils.ellipsis
import com.akagiyui.common.utils.hasText
import com.akagiyui.common.utils.toStr
import com.akagiyui.drive.entity.User
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.minio.GetObjectResponse
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

/**
 * 请求日志 切面
 *
 * @author AkagiYui
 */
@Aspect
@Component
class RequestLogAspect {

    private val log by LoggerDelegate()
    private val objectMapper = ObjectMapper().apply {
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY) // 可以序列化私有字段
        configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false) // 序列化空对象时不抛异常
        registerKotlinModule() // 添加 Kotlin 支持
    }

    @Around("execution(@(org.springframework.web.bind.annotation.*Mapping) * *(..))")
    @Throws(Throwable::class)
    fun process(joinPoint: ProceedingJoinPoint): Any? {

        val methodSignature = joinPoint.signature as MethodSignature // 获取方法签名
        val methodName = "${joinPoint.target::class.qualifiedName}::${methodSignature.name}" // 方法名

        val requestLog = StringBuilder()
        val request = httpServletRequest ?: return joinPoint.proceed()

        val clientIp: String = request.getHeader("X-Real-IP") ?: request.remoteAddr // 客户端IP
        var requestLine = "\n $clientIp -> [${request.method}]" // HTTP请求方法
        request.contentType.hasText { requestLine += "($it)" } // 请求类型
        requestLine += request.requestURI // 请求路径
        requestLog.append(requestLine)

        // 请求params
        request.parameterMap.also { params ->
            val paramsBuffer: String = params
                .filterNot { it.key == "password" }
                .map { (key, value) -> "$key=${value.joinToString(",")}".ellipsis(100) }
                .joinToString(", ")
                .ellipsis(1000)
            requestLog.append(if (paramsBuffer.isNotBlank()) "($paramsBuffer)" else "")
        }

        // 获取方法参数
        requestLog.append("\n method -> ${methodName.compressPackageName(50)}")
        val args = joinPoint.args // 方法参数
        val paramsBuffer: String = args
            .asSequence()
            .filterNotNull()
            .map { anyToString(it).ellipsis(100) }
            .joinToString(", ") // 将参数连接成字符串
            .ellipsis(1000)
        requestLog.append("($paramsBuffer)")

        // 记录返回值与异常
        return try {
            val startTime = System.currentTimeMillis()
            joinPoint.proceed().also { returnObj ->
                val duration = System.currentTimeMillis() - startTime
                val resultLog = anyToString(returnObj).ellipsis(500)
                requestLog.append("\n result[${duration}ms] <- $resultLog")
            }
        } catch (throwable: Throwable) {
            requestLog.append("\n error <- ${throwable.message}")
            throw throwable
        } finally {
            log.debug(requestLog.toString())
        }
    }

    private fun anyToString(any: Any?): String {
        if (any == null) {
            return "null"
        }
        if (any::class.isData) {
            return any.toString()
        }
        if (any is ByteArray) {
            return any.toStr()
        }
        if (any is GetObjectResponse) {
            return any.toStr()
        }
        if (any is ResponseEntity<*>) {
            return any.toStr()
        }
        if (any is MultipartFile) {
            return "MultipartFile[${any.originalFilename}]"
        }
        if ((any is Collection<*>) && any.isNotEmpty() && (any.first() is MultipartFile)) {
            return "Collection<MultipartFile>[${any.size}]"
        }
        if (any is SseEmitter) {
            return any.toString()
        }
        if (any is User) {
            return "User[${any.id}]"
        }
        return objectMapper.writeValueAsString(any)
    }

    private val httpServletRequest: HttpServletRequest?
        get() {
            val requestAttributes = RequestContextHolder.getRequestAttributes() ?: return null
            return requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST) as HttpServletRequest?
        }

}
