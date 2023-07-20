package com.akagiyui.drive.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 服务器 API
 * @author AkagiYui
 */
@RestController
@RequestMapping("/server")
public class ServerController {

    @Value("${application.version}")
    private String version;

    /**
     * 获取服务器版本
     * @return 服务器版本
     */
    @RequestMapping("/version")
    public String getVersion() {
        return version;
    }
}
