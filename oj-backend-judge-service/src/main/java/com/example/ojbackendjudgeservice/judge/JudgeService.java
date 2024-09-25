package com.example.ojbackendjudgeservice.judge;

import com.example.ojbackendmodel.model.entity.QuestionSubmit;

/**
 * @author Ariel
 */
public interface JudgeService {

    /**
     * 进行判题
     * 先获取到题目id，然后获取到对应的提交的题目信息(代码语言等) 然后调用沙箱获取到执行结果 最后根据沙箱的执行结果 设置判题最终的信息
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);

}
