package com.akagiyui.drive.component

import org.springframework.security.core.annotation.AuthenticationPrincipal


/**
 * 当前 session 用户
 * @author AkagiYui
 */
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal
annotation class CurrentUser
