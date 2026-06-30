package com.lvwyh.reviewx.web.ao.question;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 题目解析编辑请求参数。
 */
@Schema(description = "题目解析编辑请求参数")
public class QuestionAnalysisUpdateAO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "题目ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "题目ID不能为空")
    @Size(max = 255, message = "题目ID最多255个字符")
    private String questionId;

    @Schema(description = "题目解析文本")
    private String analysisContent;

    @Schema(description = "题目解析图片Base64，仅支持一张图片")
    private String analysisImageBase64;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getAnalysisContent() { return analysisContent; }
    public void setAnalysisContent(String analysisContent) { this.analysisContent = analysisContent; }
    public String getAnalysisImageBase64() { return analysisImageBase64; }
    public void setAnalysisImageBase64(String analysisImageBase64) { this.analysisImageBase64 = analysisImageBase64; }
}
