package com.example.ojbackendmodel.model.codesandbox;

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
public class ExecuteCodeRequest {
    /**
     * 接收一组题目输入
     */
    private List<String> inputList;

    /**
     * 接收代码
     */
    private String code;

    /**
     * 接收语言
     */
    private String language;

}
