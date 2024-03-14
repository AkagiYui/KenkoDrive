package com.akagiyui.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Objects;

/**
 * 文件工具类
 *
 * @author AkagiYui
 */
public class FileUtil {

    /**
     * 获取 resources 目录下的文件
     *
     * @param path 文件路径
     */
    public static File getResourceFile(String path) {
        return new File(Objects.requireNonNull(FileUtil.class.getClassLoader().getResource(path)).getFile());
    }

    /**
     * 获取 resources 目录下的文件绝对路径
     * @param path 文件路径
     * @return 文件绝对路径
     */
    public static String getResourcePath(String path){
        String resourcePath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).toString();
        resourcePath = resourcePath.replace("file:/","").concat(path);
        return resourcePath;
    }

    /**
     * 获取 webapp 目录下的文件
     * @param path 文件路径
     * @return 完整路径
     */
    public static String getStaticResourcePath(String path){
        String resourcePath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).toString();
        resourcePath = resourcePath.replace("file:/","");
        resourcePath = resourcePath.substring(0,resourcePath.indexOf("/WEB-INF")).concat(path);
        return resourcePath;
    }

    /**
     * 读取文件内容为字符串
     *
     * @param file    文件
     * @param charset 字符集
     * @return 文件内容
     * @throws IOException 读取文件异常
     */
    public static String readFileToString(File file, String charset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charset))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            return sb.toString();
        }
    }

    /**
     * 读取文件内容为字符串，默认字符集为 utf-8
     *
     * @param file 文件
     * @return 文件内容
     * @throws IOException 读取文件异常
     */
    public static String readFileToString(File file) throws IOException {
        return readFileToString(file, "utf-8");
    }

    private FileUtil() {
    }
}
