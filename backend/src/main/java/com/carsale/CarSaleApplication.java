package com.carsale;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 汽车销售管理系统主应用程序类
 * 作为项目的入口点
 */
@SpringBootApplication
@MapperScan("com.carsale.mapper") // 扫描MyBatis映射器
public class CarSaleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarSaleApplication.class, args);
    }

    /**
     * 配置密码加密器
     * 使用BCrypt算法进行密码加密
     * @return PasswordEncoder 密码加密器实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
