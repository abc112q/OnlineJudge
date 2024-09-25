package com.example.ojbackendquestionservice.service.serviceImpl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.ojbackendcommon.common.ErrorCode;
import com.example.ojbackendcommon.constant.CommonConstant;
import com.example.ojbackendcommon.exception.BusinessException;
import com.example.ojbackendcommon.utils.SqlUtils;
import com.example.ojbackendmodel.model.dto.QuestionSubmit.QuestionSubmitAddRequest;
import com.example.ojbackendmodel.model.dto.QuestionSubmit.QuestionSubmitQueryRequest;
import com.example.ojbackendmodel.model.entity.Question;
import com.example.ojbackendmodel.model.entity.QuestionSubmit;
import com.example.ojbackendmodel.model.entity.User;
import com.example.ojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.example.ojbackendmodel.model.enums.QuestionSubmitStatusEnum;
import com.example.ojbackendmodel.model.vo.QuestionSubmitVO;
import com.example.ojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.example.ojbackendquestionservice.messageMq.MessageProducer;
import com.example.ojbackendquestionservice.service.QuestionSubmitService;
import com.example.ojbackendserviceclient.service.JudgeFeignClient;
import com.example.ojbackendserviceclient.service.QuestionFeignClient;
import com.example.ojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author Ariel
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-09-04 14:25:40
*/
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService {

    @Resource
    private QuestionFeignClient questionService;

    @Resource
    private UserFeignClient userService;

    /**
     * 注意这里产生了循环依赖
     */
    @Resource
    @Lazy
    private JudgeFeignClient JudgeService;

    @Resource
    private MessageProducer messageProducer;

    // region 提交题目
    /**
     * 本质就是题目提交表新增一条记录
     * 加锁防止用户连续点击
     * @param request
     * @param loginUser
     * @return
     */
    @Override
    public Long doQuestionSubmit(QuestionSubmitAddRequest request, User loginUser) {
        // 校验语言是否合法
        String language = request.getLanguage();
        String code = request.getCode();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if(languageEnum == null){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"编程语言不合法");
        }
        // 判断题目是否存在
        long questionId = request.getQuestionId();
        Question question = questionService.getQuestionById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setLanguage(language);
        questionSubmit.setCode(code);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(loginUser.getId());
        // 设置初始化判题信息
        questionSubmit.setJudgeInfo("{}");
        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        boolean save = this.save(questionSubmit);
        if(!save){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"提交失败");
        }
        // todo 执行判题服务（判断用户提交的题目是否符合硬性要求）
        Long id = questionSubmit.getId();
//        CompletableFuture.runAsync(() -> JudgeService.doJudge(id));
        // 发送消息
        messageProducer.sendMessage("code_exchange","my_routingKey", String.valueOf(id));
        return id;

    }
    // endregion

    // region 脱敏处理成前端可看见的
    /**
     * 拿到视图
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 这条提交题目的代码只有题目提交的本人或者管理员可见
        // User loginUser = userService.getLoginUser(request);   这里为了避免每次都查询一次登陆的用户，所以干脆将查询这一步抽到外面
        Long userId = questionSubmit.getUserId();
        if(!userId.equals(loginUser.getId()) && !userService.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 分页拿到视图
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(),
                                                                 questionSubmitPage.getTotal());
        if (CollUtil.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
       // 将获得获得到的分页记录逐行脱敏，就是去调用上面的方法即可
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }
    // endregion

    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Long userId = questionSubmitQueryRequest.getUserId();
        Integer status = questionSubmitQueryRequest.getStatus();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        // 拼接查询条件
        queryWrapper.like(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "id", questionId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status) !=null , "status", status);
        queryWrapper.eq("isDelete",false);
        // 默认升序   排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

}

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int doQuestionSubmitInner(long userId, long questionId){
//        QuestionSubmit questionSubmit = new QuestionSubmit();
//        questionSubmit.setUserId(userId);
//        questionSubmit.setQuestionId(questionId);
//        QueryWrapper<QuestionSubmit> questionQueryWrapper = new QueryWrapper<>(questionSubmit);
//        QuestionSubmit oldQuestionSubmit = this.getOne(questionQueryWrapper);
//        boolean result;
//        // 已点赞
//        if (oldQuestionSubmit != null) {
//            result = this.remove(questionQueryWrapper);
//            if (result) {
//                // 点赞数 - 1
//                result = questionService.update()
//                        .eq("id", questionId)
//                        .gt("thumbNum", 0)
//                        .setSql("thumbNum = thumbNum - 1")
//                        .update();
//                return result ? -1 : 0;
//            } else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        } else {
//            // 未点赞
//            result = this.save(questionSubmit);
//            if (result) {
//                // 点赞数 + 1
//                result = questionService.update()
//                        .eq("id", questionId)
//                        .setSql("thumbNum = thumbNum + 1")
//                        .update();
//                return result ? 1 : 0;
//            } else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        }
//    }
//}




