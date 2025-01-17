package com.example.ojbackendjudgeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.example.ojbackendjudgeservice.messageMq.InitRabbitMq.doInitMq;

/**
 * @author Ariel
 */
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
@ComponentScan("com.example")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.example.ojbackendserviceclient.service")
public class OjBackendJudgeServiceApplication {

    public static void main(String[] args) {
        // 初始化消息队列 对于本项目来说 如果消息队列初始化失败就不应该让项目启动 所以放在这里
        doInitMq();
        SpringApplication.run(OjBackendJudgeServiceApplication.class, args);
    }

}
