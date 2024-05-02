package com.akagiyui.drive.component

import com.akagiyui.common.delegate.LoggerDelegate
import com.akagiyui.common.utils.compressPackageName
import com.akagiyui.common.utils.ellipsis
import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.multipart.MultipartFile

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

    @Around("execution(* com.akagiyui.drive.controller.*.*(..))")
    @Throws(Throwable::class)
    fun process(joinPoint: ProceedingJoinPoint): Any? {

        val methodSignature = joinPoint.signature as MethodSignature // 获取方法签名
        val methodName = "${joinPoint.target::class.qualifiedName}::${methodSignature.name}" // 方法名

        val requestLog = StringBuilder()
        val request = httpServletRequest ?: return joinPoint.proceed()

        // 获取客户端IP
        val clientIp: String = request.getHeader("X-Real-IP") ?: request.remoteAddr
        requestLog.append("\n $clientIp -> [${request.method}]${request.requestURI}")
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
            .map {
                if (it is MultipartFile) {
                    "MultipartFile[${it.originalFilename}]"
                } else if (it is Collection<*> && it.isNotEmpty() && it.first() is MultipartFile) {
                    "Collection<MultipartFile>[${it.size}]"
                } else {
                    objectMapper.writeValueAsString(it).ellipsis(100) // 序列化并截断参数
                }
            }
            .joinToString(", ") // 将参数连接成字符串
            .ellipsis(1000)
        requestLog.append("($paramsBuffer)")

        // 记录返回值与异常
        return try {
            val startTime = System.currentTimeMillis()
            joinPoint.proceed().also { returnObj ->
                val duration = System.currentTimeMillis() - startTime
                val resultLog = returnObj?.let { objectMapper.writeValueAsString(it).ellipsis(500) } ?: "null"
                requestLog.append("\n result[${duration}ms] <- $resultLog")
            }
        } catch (throwable: Throwable) {
            requestLog.append("\n error <- ${throwable.message}")
            throw throwable
        } finally {
            log.debug(requestLog.toString())
        }
    }

    private val httpServletRequest: HttpServletRequest?
        get() {
            val requestAttributes = RequestContextHolder.getRequestAttributes() ?: return null
            return requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST) as HttpServletRequest?
        }

}
