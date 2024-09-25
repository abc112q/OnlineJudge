package com.example.ojbackenduserservice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Ariel
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.example.ojbackenduserservice.mapper")
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@ComponentScan("com.example")
@EnableDiscoveryClient
// 当你的服务要使用feignClient的时候，就会自动找到已有的bean的服务类
@EnableFeignClients(basePackages = "com.example.ojbackendserviceclient.service")
public class OjBackendUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OjBackendUserServiceApplication.class, args);
    }

}
