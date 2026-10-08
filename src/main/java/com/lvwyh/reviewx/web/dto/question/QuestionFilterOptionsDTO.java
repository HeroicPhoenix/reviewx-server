package com.lvwyh.reviewx.web.dto.question;

import java.util.List;

public class QuestionFilterOptionsDTO {
    private final List<String> questionTypes;
    private final List<String> questionYears;
    private final List<String> questionSources;
    private final List<String> questionJoinDates;

    public QuestionFilterOptionsDTO(List<String> questionTypes, List<String> questionYears, List<String> questionSources, List<String> questionJoinDates) {
        this.questionTypes = questionTypes;
        this.questionYears = questionYears;
        this.questionSources = questionSources;
        this.questionJoinDates = questionJoinDates;
    }

    public List<String> getQuestionTypes() { return questionTypes; }

    public List<String> getQuestionYears() { return questionYears; }

    public List<String> getQuestionSources() { return questionSources; }

    public List<String> getQuestionJoinDates() { return questionJoinDates; }
}
