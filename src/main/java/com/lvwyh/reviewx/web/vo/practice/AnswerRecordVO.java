package com.lvwyh.reviewx.web.vo.practice;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class AnswerRecordVO implements Serializable {

    private Long answerRecordId;
    private String questionId;
    private List<String> selectedAnswer;
    private List<String> correctAnswer;
    private Boolean isCorrect;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
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
