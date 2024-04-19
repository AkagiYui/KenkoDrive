package com.akagiyui.drive.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 头像内容
 *
 * @author AkagiYui
 */
@Setter
@Getter
@AllArgsConstructor
public class AvatarContent {
    private byte[] content;
    private String contentType;
}
