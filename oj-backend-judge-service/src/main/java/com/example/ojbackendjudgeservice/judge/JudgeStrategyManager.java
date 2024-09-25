package com.example.ojbackendjudgeservice.judge;

import com.example.ojbackendjudgeservice.judge.strategy.DefaultJudgeStrategy;
import com.example.ojbackendjudgeservice.judge.strategy.JavaLanguageJudgeStrategy;
import com.example.ojbackendjudgeservice.judge.strategy.JudgeContext;
import com.example.ojbackendjudgeservice.judge.strategy.JudgeStrategy;
import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import org.springframework.stereotype.Service;

/**
 * @author Ariel
 */
@Service
public class JudgeStrategyManager {
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        final String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if (language.equals(QuestionSubmitLanguageEnum.JAVA.getValue())){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
