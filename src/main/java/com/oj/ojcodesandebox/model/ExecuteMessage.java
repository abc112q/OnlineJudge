package com.oj.ojcodesandebox.model;

import lombok.Data;

/**
 * @author Ariel
 * 编译输出的信息
 */
@Data
public class ExecuteMessage {

    /**
     * 不能使用基本数据类型
     * int默认值为0，与正常退出的退出码一样了
     */
    private Integer exitCode;

    // 正常信息
    private String message;

    private String errorMessage;

    private Long memory;

    private Long time;
}
