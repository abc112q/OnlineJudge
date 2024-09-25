package com.example.ojbackendjudgeservice.judge.codesandbox.impl;

import com.example.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * @author Ariel
 * 使用第三方的代码沙箱
 */
public class ThirdPartyCodeSandBoxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
