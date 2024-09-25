package com.example.ojbackendmodel.model.dto.Question;

import lombok.Data;

/**
 * @author Ariel
 */
@Data
public class JudgeConfig {

    /**
     * 内存限制 kb
     */
    private Long memoryLimit;

    /**
     * 时间限制 ms
     */
    private Long timeLimit;

    /**
     * 堆栈限制 kb
     */
    private Long stackLimit;
}
