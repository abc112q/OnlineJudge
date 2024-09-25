package com.example.ojbackendmodel.model.codesandbox;

import lombok.Data;

/**
 * @author Ariel
 * 用户提交的题目信息
 */
@Data
public class JudgeInfo {

    /**
     * 提交代码后输出的信息
     */
    private String message;

    /**
     * 用户编写的代码占用的内存
     */
    private Long memory;

    /**
     * 用户代码运行时间
     */
    private Long time;
}
