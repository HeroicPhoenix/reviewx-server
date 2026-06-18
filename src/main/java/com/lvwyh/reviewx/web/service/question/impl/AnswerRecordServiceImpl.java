package com.lvwyh.reviewx.web.service.question.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.ao.practice.SubmitAnswerAO;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.entity.question.AnswerRecord;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.mapper.question.AnswerRecordMapper;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.AnswerRecordService;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordStatVO;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordVO;
import com.lvwyh.reviewx.web.vo.practice.SubmitAnswerVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnswerRecordServiceImpl extends QuestionConvertSupport implements AnswerRecordService {

    private final QuestionMapper questionMapper;
    private final AnswerRecordMapper answerRecordMapper;

    public AnswerRecordServiceImpl(QuestionMapper questionMapper,
                                   AnswerRecordMapper answerRecordMapper,
                                   ObjectMapper objectMapper) {
        super(objectMapper);
        this.questionMapper = questionMapper;
        this.answerRecordMapper = answerRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitAnswerVO submitAnswer(Long userId, SubmitAnswerAO ao) {
        if (ao.getEndTime().isBefore(ao.getStartTime())) {
            throw new BusinessException(400, "结束作答时间不能早于开始作答时间");
        }
        Question question = questionMapper.selectById(ao.getQuestionId());
        if (question == null) {
            throw new BusinessException(404, "题目不存在");
        }

        List<String> selected = normalizeAnswers(ao.getSelectedAnswer());
        List<String> correct = normalizeAnswers(parseAnswer(question.getAnswerContent()));
        boolean isCorrect = selected.equals(correct);

        LocalDateTime now = LocalDateTime.now();
        AnswerRecord record = new AnswerRecord();
        record.setQuestionId(question.getQuestionId());
        record.setUserId(userId);
        record.setSelectedAnswer(toAnswerJson(selected));
        record.setCorrectAnswer(toAnswerJson(correct));
        record.setIsCorrect(isCorrect ? 1 : 0);
        record.setStartTime(ao.getStartTime());
        record.setEndTime(ao.getEndTime());
        record.setCreatedTime(now);
        record.setUpdatedTime(now);
        int rows = answerRecordMapper.insert(record);
        if (rows != 1) {
            throw new BusinessException("保存答题记录失败");
        }

        SubmitAnswerVO vo = new SubmitAnswerVO();
        vo.setAnswerRecordId(record.getAnswerRecordId());
        vo.setQuestionId(record.getQuestionId());
        vo.setIsCorrect(isCorrect);
        vo.setSelectedAnswer(selected);
        vo.setCorrectAnswer(correct);
        vo.setAnswerSource(question.getAnswerSource());
        vo.setDurationMs(durationMs(ao.getStartTime(), ao.getEndTime()));
        return vo;
    }

    @Override
    public PageResult<AnswerRecordVO> listPage(Long userId, String questionId, Integer isCorrect, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        int validPageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int validPageSize = pageSize == null || pageSize < 1 ? 20 : Math.min(pageSize, 100);
        int offset = (validPageNum - 1) * validPageSize;
        long total = answerRecordMapper.countPage(userId, questionId, isCorrect, startTime, endTime);
        List<AnswerRecord> records = answerRecordMapper.selectPage(userId, questionId, isCorrect, startTime, endTime, offset, validPageSize);
        List<AnswerRecordVO> voList = new ArrayList<AnswerRecordVO>();
        if (records != null) {
            for (AnswerRecord record : records) {
                voList.add(toAnswerRecordVO(record));
            }
        }
        return new PageResult<AnswerRecordVO>(total, validPageNum, validPageSize, voList);
    }

    @Override
    public AnswerRecordStatVO stat(Long userId) {
        long total = nullToZero(answerRecordMapper.countTotal(userId));
        long correct = nullToZero(answerRecordMapper.countCorrect(userId));
        long wrong = total - correct;
        AnswerRecordStatVO vo = new AnswerRecordStatVO();
        vo.setTotalCount(total);
        vo.setCorrectCount(correct);
        vo.setWrongCount(wrong);
        vo.setAverageDurationMs(nullToZero(answerRecordMapper.avgDurationMs(userId)));
        vo.setCorrectRate(total == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(correct).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP));
        return vo;
    }

    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }
}
