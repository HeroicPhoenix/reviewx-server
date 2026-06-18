package com.lvwyh.reviewx.web.vo.question;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionVO implements Serializable {

    private String questionId;
    private String questionContent;
    private String questionImageBase64;
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    private String option6;
    private String option7;
    private String option8;
    private List<String> answerContent;
    private String answerSource;
    private String questionYear;
    private String questionSource;
    private String correctRate;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getQuestionContent() { return questionContent; }
    public void setQuestionContent(String questionContent) { this.questionContent = questionContent; }
    public String getQuestionImageBase64() { return questionImageBase64; }
    public void setQuestionImageBase64(String questionImageBase64) { this.questionImageBase64 = questionImageBase64; }
    public String getOption1() { return option1; }
    public void setOption1(String option1) { this.option1 = option1; }
    public String getOption2() { return option2; }
    public void setOption2(String option2) { this.option2 = option2; }
    public String getOption3() { return option3; }
    public void setOption3(String option3) { this.option3 = option3; }
    public String getOption4() { return option4; }
    public void setOption4(String option4) { this.option4 = option4; }
    public String getOption5() { return option5; }
    public void setOption5(String option5) { this.option5 = option5; }
    public String getOption6() { return option6; }
    public void setOption6(String option6) { this.option6 = option6; }
    public String getOption7() { return option7; }
    public void setOption7(String option7) { this.option7 = option7; }
    public String getOption8() { return option8; }
    public void setOption8(String option8) { this.option8 = option8; }
    public List<String> getAnswerContent() { return answerContent; }
    public void setAnswerContent(List<String> answerContent) { this.answerContent = answerContent; }
    public String getAnswerSource() { return answerSource; }
    public void setAnswerSource(String answerSource) { this.answerSource = answerSource; }
    public String getQuestionYear() { return questionYear; }
    public void setQuestionYear(String questionYear) { this.questionYear = questionYear; }
    public String getQuestionSource() { return questionSource; }
    public void setQuestionSource(String questionSource) { this.questionSource = questionSource; }
    public String getCorrectRate() { return correctRate; }
    public void setCorrectRate(String correctRate) { this.correctRate = correctRate; }
}
