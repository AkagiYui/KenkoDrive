package com.akagiyui.drive.component;

/**
 * 静态字段，临时使用，后续要迁移到数据库
 *
 * @author AkagiYui
 */
public class StaticField {
    /**
     * 头像文件大小的上限值(3MB)
     */
    public static final int AVATAR_MAX_SIZE = 3 * 1024 * 1024;
    /**
     * 高度限制
     */
    public static final int HEIGHT_LIMIT = 200;
    /**
     * 宽度限制
     */
    public static final int WIDTH_LIMIT = 200;
    /**
     * 头像图片格式
     */
    public static final String IMAGE_FORMAT = "jpg";

    private StaticField() {
    }
}
