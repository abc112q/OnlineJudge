package com.example.ojbackendjudgeservice.judge.codesandbox.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.example.ojbackendcommon.common.ErrorCode;
import com.example.ojbackendcommon.exception.BusinessException;
import com.example.ojbackendjudgeservice.judge.codesandbox.CodeSandbox;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.example.ojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 实际调用远程接口的代码沙箱
 * @author Ariel
 */
public class RemoteCodeSandboxImpl implements CodeSandbox {

    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_KEY = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        System.out.println("远程代码沙箱");
        // 远程接口地址
        String url = "http://localhost:8089/excuteCode";
        String json = JSONUtil.toJsonStr(request);
        String response = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER,AUTH_REQUEST_KEY)
                .body(json)
                .execute()
                .body();
        if(StrUtil.isBlank(response)){
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR ,"远程代码沙箱调用失败="+response);
        }
        return JSONUtil.toBean(response, ExecuteCodeResponse.class);
    }
}
