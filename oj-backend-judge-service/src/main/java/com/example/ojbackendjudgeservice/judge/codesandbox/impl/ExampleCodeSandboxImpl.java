package com.example.ojbackendjudgeservice.judge.codesandbox.impl;


import com.example.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.example.ojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.example.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * @author Ariel
 * 示例代码沙箱  先用这个跑通流程
 */
public class ExampleCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        System.out.println("示例代码沙箱");
        List<String> inputList = request.getInputList();
        ExecuteCodeResponse response = new ExecuteCodeResponse();
        // todo 这里想设置输入与输出相等
        response.setOutputList(inputList);
        response.setMessage("测试执行成功");
        response.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        response.setJudgeInfo(judgeInfo);
        return response;
    }
}
