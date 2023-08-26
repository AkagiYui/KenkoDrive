package com.akagiyui.drive.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 元信息控制器
 *
 * @author AkagiYui
 */
@RestController
@RequestMapping("/info")
public class MetaInfoController {

    @Value("${application.version:unknown}")
    private String version;

    /**
     * 获取服务器版本
     *
     * @return 服务器版本
     */
    @RequestMapping("/version")
    public String getVersion() {
        return version;
    }

}
