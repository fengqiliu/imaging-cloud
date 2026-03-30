package com.dsite.medical.admin.controller;

import com.dsite.medical.cloudfilm.storage.StorageService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 静态资源Controller - 用于提供影像文件访问
 */
@RestController
@RequestMapping("/uploads")
public class StaticResourceController {

    @Autowired
    @Qualifier("localStorageService")
    private StorageService storageService;

    @GetMapping("/**")
    public void getFile(HttpServletResponse response) throws IOException {
        // 获取请求路径
        String path = "/images/";
        // 这里简化处理，实际应该从request获取完整路径
        // 需要配置Spring MVC静态资源映射
    }
}
