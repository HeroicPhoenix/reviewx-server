package com.lvwyh.reviewx.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ReviewX 后端服务启动入口。
 *
 * 这里负责启动 Spring Boot 应用，并通过 @MapperScan 注册 MyBatis Mapper 接口。
 */
@SpringBootApplication
@MapperScan("com.lvwyh.reviewx.web.mapper")
public class ReviewXWebApplication {

    /**
     * JVM 主入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ReviewXWebApplication.class, args);
    }
}
