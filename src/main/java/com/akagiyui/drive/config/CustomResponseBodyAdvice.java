package com.akagiyui.drive.config;

import com.akagiyui.common.ResponseResult;
import lombok.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

/**
 * 包装类处理
 * @author AkagiYui
 */
@RestControllerAdvice
public class CustomResponseBodyAdvice implements ResponseBodyAdvice<Object>, WebMvcConfigurer {

    private static final List<Class<?>> IGNORE_CLASSES;

    static {
        // 忽略的类
        IGNORE_CLASSES = List.of(
                ResponseEntity.class, // 文件
                byte[].class, // 二进制数据
                ResponseResult.class // 已经包装过的数据
        );
    }

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
        Class<?> parameterType = returnType.getParameterType();
        return !IGNORE_CLASSES.contains(parameterType);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 把String类型的转换器去掉，不使用String类型的转换器，防止过早转换发生异常
        converters.removeIf(converter -> converter.getClass() == StringHttpMessageConverter.class);
    }
}
