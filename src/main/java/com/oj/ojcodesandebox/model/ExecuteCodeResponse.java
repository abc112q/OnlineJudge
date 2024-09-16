package com.oj.ojcodesandebox.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * @author Ariel
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteCodeResponse {
    /**
     * 响应输出
     */
    private List<String> outputList;

    /**
     * 程序执行的环境信息
     * 不是判题信息（关于题目的信息）
     */
    private String message;

    /**
     * 程序执行状态
     */
    private Integer status;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

}
