package com.lvwyh.reviewx.web.entity.question;

import java.time.LocalDateTime;

/**
 * 题目解析实体。
 *
 * 映射 question_analysis 表，与题目表按用户 ID 和题目 ID 关联。
 */
public class QuestionAnalysis {

    /** 题目字符串 ID。 */
    private String questionId;
    /** 题目归属用户 ID。 */
    private Long userId;
    /** 题目解析文本。 */
    private String analysisContent;
    /** 题目解析图片 Base64。 */
    private String analysisImageBase64;
    /** 创建时间。 */
    private LocalDateTime createdTime;
    /** 更新时间。 */
    private LocalDateTime updatedTime;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getAnalysisContent() { return analysisContent; }
    public void setAnalysisContent(String analysisContent) { this.analysisContent = analysisContent; }
    public String getAnalysisImageBase64() { return analysisImageBase64; }
    public void setAnalysisImageBase64(String analysisImageBase64) { this.analysisImageBase64 = analysisImageBase64; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
