package com.lvwyh.reviewx.web.vo.practice;

import java.io.Serializable;
import java.util.List;

/**
 * 提交答案返回对象。
 *
 * 服务端完成判题并保存记录后，返回本次作答结果和正确答案。
 */
public class SubmitAnswerVO implements Serializable {

    /** 新生成的答题记录 ID。 */
    private Long answerRecordId;
    /** 题目字符串 ID。 */
    private String questionId;
    /** 本次作答是否正确。 */
    private Boolean isCorrect;
    /** 用户提交的答案。 */
    private List<String> selectedAnswer;
    /** 正确答案。 */
    private List<String> correctAnswer;
    /** 答案来源。 */
    private String answerSource;
    /** 作答耗时，单位毫秒。 */
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
