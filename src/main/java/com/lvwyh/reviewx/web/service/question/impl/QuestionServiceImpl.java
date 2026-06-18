package com.lvwyh.reviewx.web.service.question.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.QuestionService;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl extends QuestionConvertSupport implements QuestionService {

    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper, ObjectMapper objectMapper) {
        super(objectMapper);
        this.questionMapper = questionMapper;
    }

    @Override
    public QuestionVO detail(String questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(404, "题目不存在");
        }
        return toQuestionVO(question, true, true);
    }

    @Override
    public PageResult<QuestionVO> search(String keyword, String questionYear, String questionSource, Integer pageNum, Integer pageSize) {
        int validPageNum = validPageNum(pageNum);
        int validPageSize = validPageSize(pageSize);
        int offset = (validPageNum - 1) * validPageSize;
        long total = questionMapper.countSearch(keyword, questionYear, questionSource);
        return new PageResult<QuestionVO>(total, validPageNum, validPageSize,
                toQuestionVOList(questionMapper.search(keyword, questionYear, questionSource, offset, validPageSize), true, false));
    }

    @Override
    public List<QuestionVO> randomList(String questionYear, String questionSource, Integer size) {
        return toQuestionVOList(questionMapper.selectRandom(questionYear, questionSource, validSize(size)), false, true);
    }

    @Override
    public PageResult<QuestionVO> orderList(String questionYear, String questionSource, Integer pageNum, Integer pageSize) {
        int validPageNum = validPageNum(pageNum);
        int validPageSize = validPageSize(pageSize);
        int offset = (validPageNum - 1) * validPageSize;
        long total = questionMapper.countSearch(null, questionYear, questionSource);
        return new PageResult<QuestionVO>(total, validPageNum, validPageSize,
                toQuestionVOList(questionMapper.selectOrder(questionYear, questionSource, offset, validPageSize), false, true));
    }

    @Override
    public List<QuestionVO> wrongList(Long userId, String questionYear, String questionSource, Integer size) {
        return toQuestionVOList(questionMapper.selectWrong(userId, questionYear, questionSource, validSize(size)), false, true);
    }

    private List<QuestionVO> toQuestionVOList(List<Question> questions, boolean includeAnswer, boolean includeImage) {
        List<QuestionVO> result = new ArrayList<QuestionVO>();
        if (questions == null) {
            return result;
        }
        for (Question question : questions) {
            result.add(toQuestionVO(question, includeAnswer, includeImage));
        }
        return result;
    }

    private int validPageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int validPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 100);
    }

    private int validSize(Integer size) {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }
}
