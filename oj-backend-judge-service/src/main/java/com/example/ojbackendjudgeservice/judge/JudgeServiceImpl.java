package com.example.ojbackendjudgeservice.judge;

import cn.hutool.json.JSONUtil;
import com.example.ojbackendcommon.common.ErrorCode;
import com.example.ojbackendcommon.exception.BusinessException;
import com.example.ojbackendjudgeservice.judge.codesandbox.CodeSandBoxFactory;
import com.example.ojbackendjudgeservice.judge.codesandbox.CodeSandProxy;
import com.example.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.example.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.example.ojbackendmodel.model.dto.Question.JudgeCase;
import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.example.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Ariel
 * todo 更改为策略模式 因为我们大概率会根据不同的规则修改判题策略(比如不同的语言消耗的内存和时间标准不同)
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeStrategyManager strategyManager;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 根据id获取到用户提交的题目的初步具体信息
        final QuestionSubmit quesitionByUser = questionFeignClient.getQuestionSubmitId(questionSubmitId);
        if(quesitionByUser == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        Long quesitionId = quesitionByUser.getQuestionId();
        if(quesitionId == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        Question question = questionFeignClient.getQuestionById(quesitionId);
        // 更改题目的状态（比如说可以根据状态字段防止用户重复提交）
        if(!quesitionByUser.getStatus().equals(QuestionSubmitStatusEnum.WAITING.getValue())){
            // 如果不是等待判题的状态抛出异常
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"该题正在判题中");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        // 如果更新失败直接返回
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新失败");
        }
        CodeSandbox codeSandbox =  CodeSandBoxFactory.newInstance(type);
        codeSandbox = new CodeSandProxy(codeSandbox);
        // 获取用户提交的初始题目信息
        String language = quesitionByUser.getLanguage();
        String code = quesitionByUser.getCode();
        String JudgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(JudgeCaseStr, JudgeCase.class);
        // 获取每个list中的input并收集成一个列表
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 设置题目信息到
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        // 先使用代码沙箱执行代码，然后再进行判题（将执行结果与预期结果对比） 得到的使用户结果
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        // 获取用户的输出
        List<String> outputList = executeCodeResponse.getOutputList();
        // 判断沙箱执行的输出数量是否与预期的输出数量相等； 判断每一项输出与预期输出是否相等（todo 因为我们目前的规则就是输入输出相等）； 判断题目的限制是否符合要求 ...
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setOutputList(outputList);
        judgeContext.setInputList(inputList);
        // judgeCaseList包括了系统的输入和预期输出
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(quesitionByUser);
        // todo 使用判题策略
        JudgeInfo judgeInfoResponse = strategyManager.doJudge(judgeContext);
        // 判题后修改数据库中的判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setStatus(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoResponse));
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新失败");
        }
        QuestionSubmit questionSubmitResponse = questionFeignClient.getQuestionSubmitId(questionSubmitId);
        return questionSubmitResponse;
    }
}

