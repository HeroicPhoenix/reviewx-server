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

/**
 * 题目查询服务实现。
 *
 * 统一控制题目接口是否返回答案：详情和搜索允许看答案，刷题取题不返回答案。
 */
@Service
public class QuestionServiceImpl extends QuestionConvertSupport implements QuestionService {

    /** 题目数据库访问对象。 */
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(QuestionMapper questionMapper, ObjectMapper objectMapper) {
        super(objectMapper);
        this.questionMapper = questionMapper;
    }

    /**
     * 查询题目详情。
     *
     * 该接口属于“查看题目”，因此返回正确答案和图片。
     */
    @Override
    public QuestionVO detail(String questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new BusinessException(404, "题目不存在");
        }
        return toQuestionVO(question, true, true);
    }

    /**
     * 搜索题目。
     *
     * 列表不返回图片 Base64，避免大字段拖慢分页接口。
     */
    @Override
    public PageResult<QuestionVO> search(String keyword, String questionType, String questionYear, String questionSource, Integer pageNum, Integer pageSize) {
        int validPageNum = validPageNum(pageNum);
        int validPageSize = validPageSize(pageSize);
        int offset = (validPageNum - 1) * validPageSize;
        long total = questionMapper.countSearch(keyword, questionType, questionYear, questionSource);
        return new PageResult<QuestionVO>(total, validPageNum, validPageSize,
                toQuestionVOList(questionMapper.search(keyword, questionType, questionYear, questionSource, offset, validPageSize), true, false));
    }

    /**
     * 查询题型下拉选项。
     */
    @Override
    public List<String> questionTypes() {
        return questionMapper.selectQuestionTypes();
    }

    /**
     * 随机取题。
     *
     * 用于普通练习模式，不返回正确答案。
     */
    @Override
    public List<QuestionVO> randomList(String questionType, String questionYear, String questionSource, Integer size) {
        return toQuestionVOList(questionMapper.selectRandom(questionType, questionYear, questionSource, validSize(size)), false, true);
    }

    /**
     * 顺序分页取题。
     *
     * 用于按题库顺序刷题，不返回正确答案。
     */
    @Override
    public PageResult<QuestionVO> orderList(String questionType, String questionYear, String questionSource, Integer pageNum, Integer pageSize) {
        int validPageNum = validPageNum(pageNum);
        int validPageSize = validPageSize(pageSize);
        int offset = (validPageNum - 1) * validPageSize;
        long total = questionMapper.countSearch(null, questionType, questionYear, questionSource);
        return new PageResult<QuestionVO>(total, validPageNum, validPageSize,
                toQuestionVOList(questionMapper.selectOrder(questionType, questionYear, questionSource, offset, validPageSize), false, true));
    }

    /**
     * 错题随机取题。
     *
     * 只从当前用户历史错误记录关联的题目中抽取。
     */
    @Override
    public List<QuestionVO> wrongList(Long userId, String questionType, String questionYear, String questionSource, Integer size) {
        return toQuestionVOList(questionMapper.selectWrong(userId, questionType, questionYear, questionSource, validSize(size)), false, true);
    }

    /**
     * 批量转换题目列表。
     */
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

    /**
     * 校验页码，非法页码默认第 1 页。
     */
    private int validPageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    /**
     * 校验分页大小，最大限制为 100。
     */
    private int validPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 20;
        }
        return Math.min(pageSize, 100);
    }

    /**
     * 校验抽题数量，最大限制为 100。
     */
    private int validSize(Integer size) {
        if (size == null || size < 1) {
            return 10;
        }
        return Math.min(size, 100);
    }
}
