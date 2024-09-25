package com.example.ojbackendjudgeservice.judge.strategy;

import com.example.ojbackendmodel.model.codesandbox.JudgeInfo;

/**
 * @author Ariel
 * 这些封装 设计模式就属于我们项目的架构 架构搭载好了，我们只需要写业务逻辑
 */
public interface JudgeStrategy {
    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);
}
