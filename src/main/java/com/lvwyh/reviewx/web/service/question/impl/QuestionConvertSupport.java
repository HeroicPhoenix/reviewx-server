package com.lvwyh.reviewx.web.service.question.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.entity.question.AnswerRecord;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordVO;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract class QuestionConvertSupport {

    private final ObjectMapper objectMapper;

    protected QuestionConvertSupport(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    protected QuestionVO toQuestionVO(Question question, boolean includeAnswer, boolean includeImage) {
        QuestionVO vo = new QuestionVO();
        vo.setQuestionId(question.getQuestionId());
        vo.setQuestionContent(question.getQuestionContent());
        if (includeImage) {
            vo.setQuestionImageBase64(question.getQuestionImageBase64());
        }
        vo.setOption1(question.getOption1());
        vo.setOption2(question.getOption2());
        vo.setOption3(question.getOption3());
        vo.setOption4(question.getOption4());
        vo.setOption5(question.getOption5());
        vo.setOption6(question.getOption6());
        vo.setOption7(question.getOption7());
        vo.setOption8(question.getOption8());
        if (includeAnswer) {
            vo.setAnswerContent(parseAnswer(question.getAnswerContent()));
            vo.setAnswerSource(question.getAnswerSource());
        }
        vo.setQuestionYear(question.getQuestionYear());
        vo.setQuestionSource(question.getQuestionSource());
        vo.setCorrectRate(question.getCorrectRate());
        return vo;
    }

    protected AnswerRecordVO toAnswerRecordVO(AnswerRecord record) {
        AnswerRecordVO vo = new AnswerRecordVO();
        vo.setAnswerRecordId(record.getAnswerRecordId());
        vo.setQuestionId(record.getQuestionId());
        vo.setSelectedAnswer(parseAnswer(record.getSelectedAnswer()));
        vo.setCorrectAnswer(parseAnswer(record.getCorrectAnswer()));
        vo.setIsCorrect(Integer.valueOf(1).equals(record.getIsCorrect()));
        vo.setStartTime(record.getStartTime());
        vo.setEndTime(record.getEndTime());
        vo.setDurationMs(durationMs(record.getStartTime(), record.getEndTime()));
        return vo;
    }

    protected List<String> parseAnswer(String answerJson) {
        if (!StringUtils.hasText(answerJson)) {
            return Collections.emptyList();
        }
        try {
            if (answerJson.trim().startsWith("[")) {
                return objectMapper.readValue(answerJson, new TypeReference<List<String>>() {});
            }
            List<String> single = new ArrayList<String>();
            single.add(answerJson);
            return single;
        } catch (Exception e) {
            throw new BusinessException(500, "答案JSON解析失败");
        }
    }

    protected String toAnswerJson(List<String> answers) {
        try {
            return objectMapper.writeValueAsString(normalizeAnswers(answers));
        } catch (Exception e) {
            throw new BusinessException(500, "答案JSON序列化失败");
        }
    }

    protected List<String> normalizeAnswers(List<String> answers) {
        List<String> result = new ArrayList<String>();
        if (answers != null) {
            for (String answer : answers) {
                if (StringUtils.hasText(answer)) {
                    result.add(answer.trim().toUpperCase());
                }
            }
        }
        Collections.sort(result);
        return result;
    }

    protected Long durationMs(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            return 0L;
        }
        long duration = Duration.between(startTime, endTime).toMillis();
        return Math.max(duration, 0L);
    }
}
