package com.example.ojbackendjudgeservice.judge.codesandbox;

import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * @author Ariel
 *代码沙箱的作用就是执行代码 不包括判断题目正确与否
 * 我们后面要将这个抽象成为一个单独的服务
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param request
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest request) ;
}
