package com.example.ojbackendmodel.model.dto.Question;

import lombok.Data;

/**
 * @author Ariel
 * 判题用例
 */
@Data
public class JudgeCase {

    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;
}