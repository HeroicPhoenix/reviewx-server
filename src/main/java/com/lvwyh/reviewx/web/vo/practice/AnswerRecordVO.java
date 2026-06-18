package com.lvwyh.reviewx.web.vo.practice;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 答题记录返回对象。
 *
 * 用于分页展示用户历史作答记录。
 */
public class AnswerRecordVO implements Serializable {

    /** 答题记录 ID。 */
    private Long answerRecordId;
    /** 题目字符串 ID。 */
    private String questionId;
    /** 用户选择答案。 */
    private List<String> selectedAnswer;
    /** 正确答案快照。 */
    private List<String> correctAnswer;
    /** 是否答对。 */
    private Boolean isCorrect;
    /** 开始作答时间。 */
    private LocalDateTime startTime;
    /** 结束作答时间。 */
    private LocalDateTime endTime;
    /** 作答耗时，单位毫秒。 */
    private Long durationMs;

    public Long getAnswerRecordId() { return answerRecordId; }
    public void setAnswerRecordId(Long answerRecordId) { this.answerRecordId = answerRecordId; }
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public List<String> getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(List<String> selectedAnswer) { this.selectedAnswer = selectedAnswer; }
    public List<String> getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(List<String> correctAnswer) { this.correctAnswer = correctAnswer; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
}
