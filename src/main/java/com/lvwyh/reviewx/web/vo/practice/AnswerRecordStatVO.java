package com.lvwyh.reviewx.web.vo.practice;

import java.io.Serializable;
import java.math.BigDecimal;

public class AnswerRecordStatVO implements Serializable {

    private Long totalCount;
    private Long correctCount;
    private Long wrongCount;
    private BigDecimal correctRate;
    private Long averageDurationMs;

    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public Long getCorrectCount() { return correctCount; }
    public void setCorrectCount(Long correctCount) { this.correctCount = correctCount; }
    public Long getWrongCount() { return wrongCount; }
    public void setWrongCount(Long wrongCount) { this.wrongCount = wrongCount; }
    public BigDecimal getCorrectRate() { return correctRate; }
    public void setCorrectRate(BigDecimal correctRate) { this.correctRate = correctRate; }
    public Long getAverageDurationMs() { return averageDurationMs; }
    public void setAverageDurationMs(Long averageDurationMs) { this.averageDurationMs = averageDurationMs; }
}
