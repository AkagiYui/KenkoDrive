package com.akagiyui.drive.component;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

/**
 * 请求日志 切面
 *
 * @author AkagiYui
 */
@Aspect
@Component
@Slf4j
public class RequestLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RequestLogAspect() {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY); // 可以序列化私有字段
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false); // 序列化空对象时不抛异常
    }

    @Around("execution(* com.akagiyui.drive.controller.*.*(..))")
    public Object process(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();  // 获取方法签名
            String methodName = joinPoint.getTarget().getClass().getName() + "." + methodSignature.getName();  // 方法名

            StringBuilder requestLog = new StringBuilder();
            HttpServletRequest request = getHttpServletRequest();

            // 获取客户端IP
            String clientIp = null;
            if (request != null) {
                clientIp = request.getHeader("X-Real-IP");
            }
            if (!StringUtils.hasText(clientIp) && request != null) {
                clientIp = request.getRemoteAddr();
            }
            if (request != null) {
                requestLog.append(String.format("%n %s request -> %s", clientIp, methodName));
            }

            // 获取请求参数
            Object[] args = joinPoint.getArgs();  // 方法参数
            if (args.length > 0) {
                StringBuilder paramsBuffer = new StringBuilder();
                for (Object param : args) {
                    if (param instanceof MultipartFile) {
                        continue; // 忽略文件参数
                    }
                    String paramStr = objectMapper.writeValueAsString(param);
                    if (paramStr.length() > 1000) {
                        paramStr = paramStr.substring(0, 1000) + "...";
                    }
                    paramsBuffer.append(paramStr).append(", ");
                }
                if (!paramsBuffer.isEmpty()) {
                    String paramStr =paramsBuffer.substring(0, paramsBuffer.length() - 2);
                    if (paramStr.length() > 1000) {
                        paramStr = paramStr.substring(0, 1000) + "...";
                    }
                    requestLog.append(String.format("%n params -> %s", paramStr));
                }
            }

            // 记录返回值或异常
            long startTime = System.currentTimeMillis();
            try {
                Object returnObj = joinPoint.proceed();
                if (returnObj != null) {
                    String returnStr = objectMapper.writeValueAsString(returnObj);
                    if (returnStr.length() > 1000) {
                        returnStr = returnStr.substring(0, 1000) + "...";
                    }
                    requestLog.append(String.format("%n result -> %s", returnStr));
                }
                return returnObj;
            } catch (Throwable throwable) {
                requestLog.append(String.format("%n error -> %s", throwable.getMessage()));
                throw new ServiceThrowable(throwable.getMessage(), throwable);
            } finally {
                long endTime = System.currentTimeMillis();
                requestLog.append(String.format("%n time -> %dms", endTime - startTime));
                log.debug(requestLog.toString());
            }

        } catch (ServiceThrowable serviceThrowable) {
            throw serviceThrowable.getCause();
        } catch (Throwable throwable) {
            log.error("RequestLog AOP error", throwable);
            return joinPoint.proceed();
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return (HttpServletRequest)requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
    }

    @Getter
    static class ServiceThrowable extends Throwable {
        private final Throwable cause;

        public ServiceThrowable(String message, Throwable cause) {
            super(message);
            this.cause = cause;
        }
    }
}
