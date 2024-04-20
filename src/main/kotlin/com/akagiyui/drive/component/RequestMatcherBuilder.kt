package com.akagiyui.drive.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerMappingIntrospector

/**
 * 请求地址匹配器构造器
 * <p>
 *     <a href="https://github.com/spring-projects/spring-security/issues/13568">Improve CVE-2023-34035 detection</a>
 * </p>
 *
 * @author AkagiYui
 */
@Component
class RequestMatcherBuilder(
    private val introspector: HandlerMappingIntrospector,
    private val servletPath: String?,
) {
    @Autowired
    constructor(introspector: HandlerMappingIntrospector) : this(introspector, null)

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
