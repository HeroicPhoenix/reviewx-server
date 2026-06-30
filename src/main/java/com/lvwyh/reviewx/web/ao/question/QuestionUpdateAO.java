package com.lvwyh.reviewx.web.ao.question;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 题目编辑请求参数。
 */
@Schema(description = "题目编辑请求参数")
public class QuestionUpdateAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目ID不能为空")
    @Size(max = 255, message = "题目ID最多255个字符")
    private String questionId;

    @Schema(description = "题目分类")
    @Size(max = 64, message = "题目分类最多64个字符")
    private String questionCategory;

    @Schema(description = "题干", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题干不能为空")
    private String questionContent;

    @Schema(description = "题目图片Base64，仅支持一张图片")
    private String questionImageBase64;

    @Schema(description = "选项A")
    private String option1;

    @Schema(description = "选项B")
    private String option2;

    @Schema(description = "选项C")
    private String option3;

    @Schema(description = "选项D")
    private String option4;

    @Schema(description = "选项E")
    private String option5;

    @Schema(description = "选项F")
    private String option6;

    @Schema(description = "选项G")
    private String option7;

    @Schema(description = "选项H")
    private String option8;

    @Schema(description = "正确答案JSON数组，例如 [\"A\",\"B\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    @NotEmpty(message = "答案不能为空")
    @Size(max = 8, message = "答案最多8个")
    private List<@Pattern(regexp = "^[A-H]$", message = "答案只能是A到H") String> answerContent;

    @Schema(description = "答案来源")
    @Size(max = 255, message = "答案来源最多255个字符")
    private String answerSource;

    @Schema(description = "题目解析文本")
    private String analysisContent;

    @Schema(description = "题目解析图片Base64，仅支持一张图片")
    private String analysisImageBase64;

    @Schema(description = "题目年份，4位数字")
    @Pattern(regexp = "^$|^\\d{4}$", message = "题目年份必须是4位数字")
    private String questionYear;

    @Schema(description = "题目来源")
    @Size(max = 255, message = "题目来源最多255个字符")
    private String questionSource;

    @Schema(description = "机构正确率百分比，范围0%到100%，例如49%")
    @Pattern(regexp = "^$|^(100(?:\\.0{1,2})?|(?:\\d|[1-9]\\d)(?:\\.\\d{1,2})?)%$", message = "机构正确率必须是0%到100%之间的百分比")
    private String correctRate;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
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
    public String getCorrectRate() { return correctRate; }
    public void setCorrectRate(String correctRate) { this.correctRate = correctRate; }
}
