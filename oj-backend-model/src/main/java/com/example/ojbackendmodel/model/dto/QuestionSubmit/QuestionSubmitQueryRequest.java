package com.example.ojbackendmodel.model.dto.QuestionSubmit;

import com.example.ojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 问题提交请求
 * 考虑用户提交的时候需要哪些参数
 * @author Ariel
 */
@Data
@EqualsAndHashCode(callSuper = true) // 重写父类的hashcode和equals方法
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;



    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户Id
     */
    private Long userId;

    /**
     * 提交状态
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}