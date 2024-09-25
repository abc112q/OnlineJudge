package com.example.ojbackendquestionservice.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ojbackendmodel.model.dto.QuestionSubmit.QuestionSubmitAddRequest;
import com.example.ojbackendmodel.model.dto.QuestionSubmit.QuestionSubmitQueryRequest;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendmodel.model.entity.User;
import com.example.ojbackendmodel.model.vo.QuestionSubmitVO;

/**
* @author Ariel
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2024-09-04 14:25:40
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {

    Long doQuestionSubmit(QuestionSubmitAddRequest request, User loginUser);

    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);

    // int doQuestionSubmitInner(long userId, long questionId);

    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser);

    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmit, User loginUser);

}
