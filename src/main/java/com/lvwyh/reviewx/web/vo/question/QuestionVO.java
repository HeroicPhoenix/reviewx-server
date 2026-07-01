package com.lvwyh.reviewx.web.vo.question;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * 题目返回对象。
 *
 * 详情接口会包含答案和图片；刷题接口不会包含答案，避免提前暴露正确答案。
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionVO implements Serializable {

    /** 题目字符串 ID。 */
    private String questionId;
    /** 题目类型：1单选、2多选。 */
    private Integer questionType;
    /** 题目科目分类，例如言语理解、判断推理。 */
    private String questionCategory;
    /** 题干内容。 */
    private String questionContent;
    /** 题目图片 Base64，列表接口通常不返回以减少响应体积。 */
    private String questionImageBase64;
    /** 备选答案 1。 */
    private String option1;
    private String option2;
    private String option3;
    private String option4;
    private String option5;
    private String option6;
    private String option7;
    private String option8;
    /** 正确答案列表，仅在允许查看答案的接口中返回。 */
    private List<String> answerContent;
    /** 答案来源。 */
    private String answerSource;
    /** 题目解析文本。 */
    private String analysisContent;
    /** 题目解析图片 Base64。 */
    private String analysisImageBase64;
    /** 题目年份。 */
    private String questionYear;
    /** 题目来源。 */
    private String questionSource;
    /** 题目加入日期。 */
    private LocalDate questionJoinDate;
    /** 题目正确率原始文本。 */
    private String correctRate;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public Integer getQuestionType() { return questionType; }
    public void setQuestionType(Integer questionType) { this.questionType = questionType; }
    public String getQuestionCategory() { return questionCategory; }
    public void setQuestionCategory(String questionCategory) { this.questionCategory = questionCategory; }
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
    public String getAnalysisContent() { return analysisContent; }
    public void setAnalysisContent(String analysisContent) { this.analysisContent = analysisContent; }
    public String getAnalysisImageBase64() { return analysisImageBase64; }
    public void setAnalysisImageBase64(String analysisImageBase64) { this.analysisImageBase64 = analysisImageBase64; }
    public String getQuestionYear() { return questionYear; }
    public void setQuestionYear(String questionYear) { this.questionYear = questionYear; }
    public String getQuestionSource() { return questionSource; }
    public void setQuestionSource(String questionSource) { this.questionSource = questionSource; }
    public LocalDate getQuestionJoinDate() { return questionJoinDate; }
    public void setQuestionJoinDate(LocalDate questionJoinDate) { this.questionJoinDate = questionJoinDate; }
    public String getCorrectRate() { return correctRate; }
    public void setCorrectRate(String correctRate) { this.correctRate = correctRate; }
}
