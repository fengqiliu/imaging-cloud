package com.dsite.medical.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * D-Site 云胶片管理系统启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.dsite.medical.admin", "com.dsite.medical.system", "com.dsite.medical.framework", "com.dsite.medical.cloudfilm"})
@MapperScan(basePackages = {"com.dsite.medical.admin.mapper", "com.dsite.medical.system.mapper", "com.dsite.medical.cloudfilm.mapper"})
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
