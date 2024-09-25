package com.example.ojbackendjudgeservice.judge.codesandbox;

import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ariel
 */
@Slf4j
public class CodeSandProxy implements CodeSandbox{

    private final CodeSandbox codeSandbox;

    public CodeSandProxy(CodeSandbox codeSandbox){
      this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        log.info("代码沙箱请求信息："+ request.toString());
        final ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(request);
        // todo 注意现在response就是空 因为这个方法还没有被实现
        log.info("代码沙箱响应信息:" + executeCodeResponse.toString());
        return executeCodeResponse;
    }
}
