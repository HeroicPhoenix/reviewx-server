package com.lvwyh.reviewx.web.vo.question;

import java.util.List;
import com.lvwyh.reviewx.web.dto.question.QuestionFilterOptionsDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "刷题联动筛选选项")

public class QuestionFilterOptionsVO {
    private final List<String> questionTypes;
    private final List<String> questionYears;
    private final List<String> questionSources;
    private final List<String> questionJoinDates;

    public QuestionFilterOptionsVO(QuestionFilterOptionsDTO options) {
        this.questionTypes = options.getQuestionTypes();
        this.questionYears = options.getQuestionYears();
        this.questionSources = options.getQuestionSources();
        this.questionJoinDates = options.getQuestionJoinDates();
    }

    public List<String> getQuestionTypes() { return questionTypes; }

    public List<String> getQuestionYears() { return questionYears; }

    public List<String> getQuestionSources() { return questionSources; }

    public List<String> getQuestionJoinDates() { return questionJoinDates; }
}
