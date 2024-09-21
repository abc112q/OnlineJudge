package com.oj.ojcodesandebox.controller;

import com.oj.ojcodesandebox.JavaNativeCodeSandbox;
import com.oj.ojcodesandebox.model.ExecuteCodeRequest;
import com.oj.ojcodesandebox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Ariel
 */
@RestController("/")
public class MainController {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    // 最好使用md5等加密
    private static final String AUTH_REQUEST_KEY = "secretKey";

    @Resource
    private JavaNativeCodeSandbox javaNativeCodeSandbox;

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }

    /**
     * 提供一个开放的API(直接暴露CodeSandBox的接口)
     * @param request
     * @return
     */
    @PostMapping("/excuteCode")
    ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest request, HttpServletRequest servletRequest,
                                    HttpServletResponse response){
        final String authHeader = servletRequest.getHeader(AUTH_REQUEST_HEADER);
        // 可能为空的放在括号里面
        if(!AUTH_REQUEST_KEY.equals(authHeader)){
            response.setStatus(403);
            return null;
        }
        if(request == null){
            throw new RuntimeException("request is null");
        }
        final ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandbox.executeCode(request);
        return executeCodeResponse;
    }

}
