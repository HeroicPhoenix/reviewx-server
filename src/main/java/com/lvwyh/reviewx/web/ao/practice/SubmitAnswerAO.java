package com.lvwyh.reviewx.web.ao.practice;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "提交答案入参")
public class SubmitAnswerAO implements Serializable {

    @NotEmpty(message = "题目ID不能为空")
    private String questionId;

    @NotEmpty(message = "选择答案不能为空")
    private List<String> selectedAnswer;

    @NotNull(message = "开始作答时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束作答时间不能为空")
    private LocalDateTime endTime;

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public List<String> getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(List<String> selectedAnswer) { this.selectedAnswer = selectedAnswer; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
