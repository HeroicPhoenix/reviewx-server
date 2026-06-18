package com.lvwyh.reviewx.web.vo.practice;

import java.io.Serializable;
import java.util.List;

public class SubmitAnswerVO implements Serializable {

    private Long answerRecordId;
    private String questionId;
    private Boolean isCorrect;
    private List<String> selectedAnswer;
    private List<String> correctAnswer;
    private String answerSource;
    private Long durationMs;

    public Long getAnswerRecordId() { return answerRecordId; }
    public void setAnswerRecordId(Long answerRecordId) { this.answerRecordId = answerRecordId; }
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }
    public List<String> getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(List<String> selectedAnswer) { this.selectedAnswer = selectedAnswer; }
    public List<String> getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(List<String> correctAnswer) { this.correctAnswer = correctAnswer; }
    public String getAnswerSource() { return answerSource; }
    public void setAnswerSource(String answerSource) { this.answerSource = answerSource; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
}
