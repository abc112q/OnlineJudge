package com.example.ojbackendquestionservice.controller.inner;

import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendquestionservice.service.QuestionService;
import com.example.ojbackendquestionservice.service.QuestionSubmitService;
import com.example.ojbackendserviceclient.service.QuestionFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Ariel
 * 仅仅是服务之间内部互相调用的接口
 * 这里使用的UserService继承了MyBatisPlus的Iservice接口，我们重写方法就直接调用mybatisplus现呈的方法即可
 */
@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam long questionId){
        return questionService.getById(questionId);
    }

    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitId(@RequestParam long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmit){
        return questionSubmitService.updateById(questionSubmit);
    }
}