package com.example.ojbackendjudgeservice.messageMq;

import com.example.ojbackendcommon.common.ErrorCode;
import com.example.ojbackendcommon.exception.BusinessException;
import com.example.ojbackendjudgeservice.judge.JudgeService;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

import static com.example.ojbackendcommon.constant.MqConstant.CODE_QUEUE;


/**
 * @author Ariel
 */
@Component
@Slf4j
public class MessageConsumer {

    @Resource
    private JudgeService judgeService;

    @SneakyThrows
    // 注解指定消费者监听的队列 delivery tag 是一个唯一标识消息的标识符
    @RabbitListener(queues = {CODE_QUEUE},ackMode = "MANUAL")
    public void recieveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG)long deliveryTag){
        log.info("recieve Message = {}",message);
        // 刚刚在提交用户代码的模块中将用户代码的id作为消息，发送到消息队列中
        if (message == null) {
            // 消息为空，则拒绝消息（不重试），进入死信队列
            channel.basicNack(deliveryTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息为空");
        }
        long questionSubmitId = Long.parseLong(message);
        try{
            // 进行消费（对消息进行处理【其实就是进行判题】）
            QuestionSubmit questionSubmit = judgeService.doJudge(questionSubmitId);
            if(questionSubmit != null){
                // 第二个参数表示消息不会被重新分发
                channel.basicAck(deliveryTag,false);
            }else{
                log.error("判题结果为空");
                channel.basicNack(deliveryTag,false,false);
            }
        }catch (Exception e){
            // true代表消息失败会requeue   别重新入队 可能会死循环 todo 可以解决一下
            channel.basicNack(deliveryTag,false,false);
        }
    }
}
