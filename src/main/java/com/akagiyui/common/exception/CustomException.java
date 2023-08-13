package com.akagiyui.common.exception;

import com.akagiyui.common.ResponseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 自定义异常
 * @author AkagiYui
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomException extends RuntimeException {
    /**
     * 响应状态枚举
     */
    private ResponseEnum status;
}
