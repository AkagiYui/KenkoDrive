package com.akagiyui.common

import com.akagiyui.common.delegate.LoggerDelegate
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

/**
 * 响应包装体
 *
 * @param T 数据类型
 * @property code 状态码
 * @property msg 消息
 * @property data 数据
 */
data class ResponseResult<T>(
    val code: Int,
    val msg: String,
    val data: T? = null,
) {
    companion object {
        private val log by LoggerDelegate()

        /**
         * 通用响应
         */

        fun <T> response(code: Int, msg: String, data: T? = null): ResponseResult<T> {
            return ResponseResult(code, msg, data)
        }

        /**
         * 通用响应
         */
        fun <T> response(status: ResponseEnum): ResponseResult<T> {
            return response(status.code, status.msg, null)
        }

        /**
         * 通用响应
         */
        fun <T> response(status: ResponseEnum, data: T?): ResponseResult<T> {
            return if (status == ResponseEnum.BAD_REQUEST && data is String && data.isNotBlank()) {
                response(status.code, data, null)
            } else {
                response(status.code, status.msg, data)
            }
        }

        /**
         * 成功响应
         */
        fun <T> success(data: T? = null): ResponseResult<T> {
            return response(ResponseEnum.SUCCESS, data)
        }

        /**
         * 把响应内容写入响应体
         */
        fun writeResponse(res: HttpServletResponse, code: HttpStatus, status: ResponseEnum) {
            res.status = code.value()
            res.contentType = "application/json;charset=UTF-8"
            res.characterEncoding = "UTF-8"
            try {
                val objectMapper = jacksonObjectMapper()
                res.writer.write(objectMapper.writeValueAsString(response(status, null)))
            } catch (e: Exception) {
                log.error("Write response error", e)
            }
        }

        /**
         * 把响应内容写入响应体
         */
        fun writeResponse(res: HttpServletResponse, code: HttpStatus, obj: Any) {
            res.status = code.value()
            res.contentType = "application/json;charset=UTF-8"
            res.characterEncoding = "UTF-8"
            try {
                val objectMapper = jacksonObjectMapper()
                res.writer.write(objectMapper.writeValueAsString(response(ResponseEnum.SUCCESS, obj)))
            } catch (e: Exception) {
                log.error("Write response error", e)
            }
        }
    }
}
