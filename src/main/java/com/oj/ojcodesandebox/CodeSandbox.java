package com.oj.ojcodesandebox;

import com.oj.ojcodesandebox.model.ExecuteCodeRequest;
import com.oj.ojcodesandebox.model.ExecuteCodeResponse;

import java.io.IOException;

/**
 * @author Ariel
 *代码沙箱的作用就是执行代码
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param request
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest request);
}
