package com.lvwyh.reviewx.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.mapper.question.QuestionAnalysisMapper;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.impl.QuestionJsonTransferServiceImpl;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 导出必须拒绝不可恢复的损坏答案数据。 */
class QuestionJsonTransferServiceImplExportTest {

    @Test
    void malformedAnswerJsonMustFailExportInsteadOfWritingEmptyArray() {
        QuestionMapper questionMapper = mock(QuestionMapper.class);
        QuestionAnalysisMapper analysisMapper = mock(QuestionAnalysisMapper.class);
        QuestionJsonTransferServiceImpl service = new QuestionJsonTransferServiceImpl(
                questionMapper, analysisMapper, new ObjectMapper());
        Question question = new Question();
        question.setQuestionId("broken-answer");
        question.setAnswerContent("not-json");
        when(questionMapper.countForExport(9L)).thenReturn(1L);
        when(questionMapper.selectForExportPage(9L, 0L, 100))
                .thenReturn(Collections.singletonList(question));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.exportJsonTo(9L, "question", new ByteArrayOutputStream(), null));

        assertEquals(500, error.getCode());
        assertTrue(error.getMessage().contains("broken-answer"));
        assertTrue(error.getMessage().contains("导出已终止"));
    }
}
