package com.lvwyh.reviewx.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lvwyh.reviewx.web.mapper")
public class ReviewXWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewXWebApplication.class, args);
    }
}
