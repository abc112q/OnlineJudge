package com.example.ojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.example.ojbackendmodel.model.dto.Question.JudgeCase;
import com.example.ojbackendmodel.model.dto.Question.JudgeConfig;
import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * 比如说java语言执行的时间多出十秒，那么判题策略就要调整
 * @author Ariel
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy{

    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> outputList = judgeContext.getOutputList();
        List<String> inputList = judgeContext.getInputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Long responseMemory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long responseTime = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        //返回数据
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        judgeInfoResponse.setMemory(responseMemory);
        judgeInfoResponse.setTime(responseTime);
        if(outputList.size() != inputList.size()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            // 这里需要返回的是代码沙箱执行出来的信息
            return judgeInfoResponse;
        }
        // 从数量上讲一个输入对应一个输出 且用户输出应该与系统输出相等
        for (int i = 0; i < judgeCaseList.size(); i++){
            JudgeCase judgeCase = judgeCaseList.get(i);
            // 如果数据库存放的与代码沙箱执行后相应的输出不相等
            if(!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig expectedJudgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        if(expectedJudgeConfig.getMemoryLimit() < responseMemory){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // todo java程序需要额外多编译10s （总时间-编译时间=程序本身的真正时间）
        long JAVA_PROGRAM_TIME_COST = 10000L;
        if(expectedJudgeConfig.getTimeLimit() - JAVA_PROGRAM_TIME_COST < responseTime){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 返回默认值AC
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
