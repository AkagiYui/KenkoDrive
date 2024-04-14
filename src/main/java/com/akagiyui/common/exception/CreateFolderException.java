package com.akagiyui.common.exception;

/**
 * 创建文件夹异常
 *
 * @author AkagiYui
 */
public class CreateFolderException extends RuntimeException {
    public CreateFolderException(String message) {
        super(message);
    }
}
