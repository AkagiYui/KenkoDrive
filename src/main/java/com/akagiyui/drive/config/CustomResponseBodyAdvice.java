package com.akagiyui.drive.config;

import com.akagiyui.drive.entity.ResponseResult;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 包装类处理
 * @author AkagiYui
 */
@RestControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 相应数据包装
     * @param body 返回的数据
     * @param returnType 返回的数据类型
     * @param selectedContentType 返回的 Content-Type
     * @param selectedConverterType 返回的数据类型
     * @param request 请求
     * @param response 响应
     * @return 包装后的数据
     */
    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType, @NonNull MediaType selectedContentType, @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response) {
        if (body instanceof ResponseResult) {
            // 如果返回的数据已经是包装过的，就不需要再次包装
            return body;
        }
        // 包装返回的数据
        return ResponseResult.success(body);
    }

    /**
     * 判断是否需要执行 beforeBodyWrite 方法
     * @param returnType 返回的数据类型
     * @param converterType 返回的数据类型
     * @return 是否需要执行 beforeBodyWrite 方法
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType, @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}
