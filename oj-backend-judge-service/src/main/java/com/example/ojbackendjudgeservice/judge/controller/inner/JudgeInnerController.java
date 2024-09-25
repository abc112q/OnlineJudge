package com.example.ojbackendjudgeservice.judge.controller.inner;

import com.example.ojbackendjudgeservice.judge.JudgeService;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendserviceclient.service.JudgeFeignClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author Ariel
 * 仅仅是服务之间内部互相调用的接口
 * 这里使用的UserService继承了MyBatisPlus的Iservice接口，我们重写方法就直接调用mybatisplus现呈的方法即可
 */
@RestController
@RequestMapping("/inner") // 这里使用的是内部调用，所以使用inner
public class JudgeInnerController implements JudgeFeignClient {

    @Resource
    private JudgeService judgeService;


    @Override
    @PostMapping("/do")
    public QuestionSubmit doJudge(long questionSubmitId) {
        return judgeService.doJudge(questionSubmitId);
    }
}