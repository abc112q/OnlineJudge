package com.example.ojbackendmodel.model.dto.QuestionSubmit;

import lombok.Data;

import java.io.Serializable;

/**
 * 问题提交请求
 * 考虑用户提交的时候需要哪些参数
 * @author Ariel
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 用户代码
     */
    private String code;


    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}