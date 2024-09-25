package com.example.ojbackendjudgeservice.messageMq;

import com.example.ojbackendcommon.common.ErrorCode;
import com.example.ojbackendcommon.exception.BusinessException;
import com.example.ojbackendjudgeservice.judge.JudgeService;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.example.ojbackendserviceclient.service.QuestionFeignClient;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.example.ojbackendcommon.constant.MqConstant.CODE_DLX_QUEUE;

/**
 * 如果代码执行失败（判题失败）将消息丢入死信队列
 * 丢入私信队列后 更新题目状态为失败
 * @author Ariel
 */
@Component
@Slf4j
public class CodeFailConsumer {

    @Resource
    private QuestionFeignClient questionFeignClient;

     @SneakyThrows
    @RabbitListener(queues = {CODE_DLX_QUEUE},ackMode = "MANUAL")
    public void recieveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG)long deliveryTag){
        log.info("死信队列接收到消息 = {}",message);
        if(StringUtils.isBlank(message)){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"消息为空");
        }
        //将用户代码的id作为消息，发送到队列中
        long questionSubmitId = Long.parseLong(message);
         QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitId(questionSubmitId);

            // 对消息进行处理，修改用户提交的状态为失败
            if(questionSubmit == null){
                channel.basicNack(deliveryTag,false,false);
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"题目提交信息不存在");
            }
            questionSubmit.setStatus(QuestionSubmitStatusEnum.FAILED.getValue());
             boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmit);
             if(!update){
                 log.info("处理死信队列中的消息失败，对应用户题目提交id为={}",questionSubmit.getId());
                 throw new BusinessException(ErrorCode.PARAMS_ERROR,"死信队列中的消息失败");
             }
             // 确认消息
             channel.basicAck(deliveryTag,false);
    }
}


