package com.example.ojbackendserviceclient.service;

import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Ariel
 */
@FeignClient(name = "oj-backend-judge-service",path = "/api/judge/inner")
public interface JudgeFeignClient {

    /**
     * 进行判题
     * 先获取到题目id，然后获取到对应的提交的题目信息(代码语言等) 然后调用沙箱获取到执行结果 最后根据沙箱的执行结果 设置判题最终的信息
     * @return
     */
    @PostMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);

}
