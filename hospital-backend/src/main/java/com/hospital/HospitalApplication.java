package com.hospital;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 医院预约挂号系统启动类
 * 
 * @author 徐凌
 * @version 1.0.0
 * @date 2026-03-14
 */
@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
@MapperScan("com.hospital.mapper")
public class HospitalApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalApplication.class, args);
        System.out.println("==========================================");
        System.out.println("    医院预约挂号系统启动成功！");
        System.out.println("    后端接口地址: http://localhost:8080/api");
        System.out.println("    Swagger文档: http://localhost:8080/api/swagger-ui/");
        System.out.println("    Druid监控: http://localhost:8080/api/druid/");
        System.out.println("==========================================");
    }
}
