package com.example.ojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.ojbackendmodel.model.dto.Question.QuestionQueryRequest;
import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;


/**
* @author Ariel
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2024-09-04 14:25:01
*/
public interface QuestionService extends IService<Question> {

    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    void validQuestion(Question question, boolean b);

    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);

    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}
