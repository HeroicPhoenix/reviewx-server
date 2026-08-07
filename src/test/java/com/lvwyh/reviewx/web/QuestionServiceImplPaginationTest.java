package com.lvwyh.reviewx.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.mapper.question.QuestionAnalysisMapper;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.impl.QuestionServiceImpl;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 分页偏移量必须使用 long，避免超大页码发生 int 溢出。 */
class QuestionServiceImplPaginationTest {

    @Test
    void maximumIntegerPageNumberMustProducePositiveLongOffset() {
        QuestionMapper questionMapper = mock(QuestionMapper.class);
        QuestionAnalysisMapper analysisMapper = mock(QuestionAnalysisMapper.class);
        QuestionServiceImpl service = new QuestionServiceImpl(questionMapper, analysisMapper, new ObjectMapper());
        when(questionMapper.search(1L, null, null, null, null, null, 214748364600L, 100))
                .thenReturn(Collections.emptyList());

        service.search(1L, null, null, null, null, null, Integer.MAX_VALUE, 100);

        verify(questionMapper).search(1L, null, null, null, null, null, 214748364600L, 100);
    }
}
