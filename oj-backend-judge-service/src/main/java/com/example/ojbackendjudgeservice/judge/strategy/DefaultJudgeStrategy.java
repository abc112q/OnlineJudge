package com.example.ojbackendjudgeservice.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.example.ojbackendmodel.model.dto.Question.JudgeCase;
import com.example.ojbackendmodel.model.dto.Question.JudgeConfig;
import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.enums.JudgeInfoMessageEnum;

import java.util.List;

/**
 * @author Ariel
 * 默认判题策略
 * 先使用代码沙箱执行代码，然后再进行判题（将执行结果与预期结果对比）
 */
public class DefaultJudgeStrategy implements JudgeStrategy {
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        List<String> outputList = judgeContext.getOutputList();
        List<String> inputList = judgeContext.getInputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();
        Long responseMemory = judgeInfo.getMemory();
        Long responseTime = judgeInfo.getTime();
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        //返回数据
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        judgeInfoResponse.setMemory(responseMemory);
        judgeInfoResponse.setTime(responseTime);
        if(outputList.size() != inputList.size()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            // 这里需要返回的是代码沙箱执行出来的信息
            return judgeInfoResponse;
        }
        for (int i = 0; i < judgeCaseList.size(); i++){
            JudgeCase judgeCase = judgeCaseList.get(i);
            // 如果数据库存放的与代码沙箱执行后相应的输出不相等
            if(!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                return judgeInfoResponse;
            }
        }
        // 判断题目限制

        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig expectedJudgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        if(expectedJudgeConfig.getMemoryLimit() < responseMemory){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            return judgeInfoResponse;
        }
        if(expectedJudgeConfig.getTimeLimit() < responseTime){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            return judgeInfoResponse;
        }
        // 返回默认值AC
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
