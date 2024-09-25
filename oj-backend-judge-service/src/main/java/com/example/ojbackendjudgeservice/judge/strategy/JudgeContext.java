package com.example.ojbackendjudgeservice.judge.strategy;

import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.example.ojbackendmodel.model.dto.Question.JudgeCase;
import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Ariel
 * 上下文（用于定义在策略中传递的参数）
 * 一开始不知道这里要写什么参数，然后将之前实现的判题逻辑复制到策略中
 * 然后看爆红的就是需要在这里添加的参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {

    /**
     * 最后真正的判题结果封装
     */
    private JudgeInfo judgeInfo;

    /**
     * 用户输出
     */
    private List<String> outputList;

    /**
     * 系统输入
     */
    private List<String> inputList;

    /**
     * 系统输入+系统预期输出
     */
    private List<JudgeCase> judgeCaseList;

    private Question question;

    /**
     * 用户提交的初步信息
     */
    private QuestionSubmit questionSubmit;
}
