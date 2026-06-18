package com.lvwyh.reviewx.web.vo.practice;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 答题统计返回对象。
 *
 * 统计维度限定为当前登录用户。
 */
public class AnswerRecordStatVO implements Serializable {

    /** 总作答次数。 */
    private Long totalCount;
    /** 答对次数。 */
    private Long correctCount;
    /** 答错次数。 */
    private Long wrongCount;
    /** 正确率，取值范围 0 到 1，保留四位小数。 */
    private BigDecimal correctRate;
    /** 平均作答耗时，单位毫秒。 */
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
