package com.lvwyh.reviewx.web.entity.question;

import java.time.LocalDateTime;

/**
 * 答题记录实体。
 *
 * 映射 ANSWER_RECORD 表，只记录用户真正提交答案的行为，不记录浏览题目的行为。
 */
public class AnswerRecord {

    /** 答题记录主键。 */
    private Long answerRecordId;
    /** 题目字符串 ID。 */
    private String questionId;
    /** 作答用户 ID。 */
    private Long userId;
    /** 练习会话 ID，当前预留。 */
    private String sessionId;
    /** 用户选择答案 JSON 字符串。 */
    private String selectedAnswer;
    /** 作答时正确答案快照 JSON 字符串。 */
    private String correctAnswer;
    /** 是否答对：0错误、1正确。 */
    private Integer isCorrect;
    /** 开始作答时间。 */
    private LocalDateTime startTime;
    /** 结束作答时间。 */
    private LocalDateTime endTime;
    /** 创建时间。 */
    private LocalDateTime createdTime;
    /** 更新时间。 */
    private LocalDateTime updatedTime;

    public Long getAnswerRecordId() { return answerRecordId; }
    public void setAnswerRecordId(Long answerRecordId) { this.answerRecordId = answerRecordId; }
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }
    public Integer getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Integer isCorrect) { this.isCorrect = isCorrect; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
}
