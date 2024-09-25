package com.example.ojbackendjudgeservice.messageMq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static com.example.ojbackendcommon.constant.MqConstant.*;

/**
 * @author Ariel
 * 初始化创建队列等
 */
@Slf4j
public class InitRabbitMq {

    public static void doInitMq() {
        try {
            // 配置mq信息
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("127.0.0.1");
            factory.setPassword("guest");
            factory.setUsername("guest");
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            String EXCHANGE_NAME = CODE_EXCHANGE_NAME;
            // 第二个指明了交换机的类型
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            // 创建队列
            String codeQueue = CODE_QUEUE;
            // 死信队列的配置
            Map<String,Object> codeMap = new HashMap<>();
            codeMap.put("x-dead-letter-exchange",CODE_DLX_EXCHANGE);
            codeMap.put("x-dead-letter-routing-key",CODE_ROUTING_KEY);
            // 将我们处理用户代码的队列于私信交换机绑定
            channel.queueDeclare(codeQueue, true, false, false, codeMap);
            channel.queueBind(codeQueue, EXCHANGE_NAME, "my_routingKey");

            // 创建死信队列和死信交换机
            channel.queueDeclare(CODE_DLX_QUEUE, true, false, false, null);
            channel.exchangeDeclare(CODE_DLX_EXCHANGE, CODE_DIRECT_EXCHANGE);
            // 死信队列绑定死信交换机
            channel.queueBind(CODE_DLX_QUEUE, CODE_DLX_EXCHANGE, CODE_ROUTING_KEY);

            log.info("消息队列启动成功");
        } catch (Exception e) {
            log.error("消息队列启动失败", e);
        }
    }

    public static void main(String[] args) {
        doInitMq();
    }
}