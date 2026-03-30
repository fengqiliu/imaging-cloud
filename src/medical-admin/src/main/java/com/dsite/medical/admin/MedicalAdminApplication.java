package com.dsite.medical.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * D-Site 云胶片管理系统启动类
 */
@SpringBootApplication
public class MedicalAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalAdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("  D-Site 云胶片管理系统启动成功!");
        System.out.println("  API文档: http://localhost:8080/doc.html");
        System.out.println("  Swagger: http://localhost:8080/swagger-ui.html");
        System.out.println("========================================");
    }
}
