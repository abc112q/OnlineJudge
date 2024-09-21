package com.oj.ojcodesandebox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import com.oj.ojcodesandebox.model.ExecuteCodeRequest;
import com.oj.ojcodesandebox.model.ExecuteCodeResponse;
import com.oj.ojcodesandebox.model.ExecuteMessage;
import com.oj.ojcodesandebox.model.JudgeInfo;
import com.oj.ojcodesandebox.utils.ProcessUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author Ariel
 * 核心是使用程序代替人工在命令行输入命令
 * 要使用到进程管理类Process
 * input是输入用例
 * output则是程序运行后的实际输出
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandBoxTemplate {

    /**
     * java原生实现 直接复用模板方法
     * @param executeCodeRequest
     * @return
     */
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }

}
