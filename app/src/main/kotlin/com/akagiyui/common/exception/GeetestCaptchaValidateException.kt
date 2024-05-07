package com.akagiyui.common.exception

/**
 * 极验验证码验证异常
 * @author AkagiYui
 */

class GeetestCaptchaValidateException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(
        message: String?,
        cause: Throwable?,
        enableSuppression: Boolean,
        writableStackTrace: Boolean,
    ) : super(message, cause, enableSuppression, writableStackTrace)
}
