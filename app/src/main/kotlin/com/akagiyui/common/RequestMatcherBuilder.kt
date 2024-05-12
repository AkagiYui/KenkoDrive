package com.akagiyui.common

import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

/**
 * 请求地址匹配器构造器
 * <p>
 *     <a href="https://github.com/spring-projects/spring-security/issues/13568">Improve CVE-2023-34035 detection</a>
 * </p>
 *
 * @author AkagiYui
 */
class RequestMatcherBuilder(
    private val introspector: HandlerMappingIntrospector,
    private val servletPath: String? = null,
) {

    fun matchers(vararg patterns: String): Array<MvcRequestMatcher> {
        return Array(patterns.size) { index ->
            MvcRequestMatcher(introspector, patterns[index]).apply {
                servletPath?.let { setServletPath(it) }
            }
        }
    }

    fun servletPath(path: String): RequestMatcherBuilder {
        return RequestMatcherBuilder(introspector, path)
    }
}
