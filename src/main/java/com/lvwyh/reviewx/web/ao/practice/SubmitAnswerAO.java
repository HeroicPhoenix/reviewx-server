package com.lvwyh.reviewx.web.ao.practice;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 提交答案请求参数。
 *
 * 前端在用户确认作答时提交题目 ID、所选答案和作答起止时间。
 */
@Schema(description = "提交答案入参")
public class SubmitAnswerAO implements Serializable {

    /** 题目字符串 ID，来自题库 JSON。 */
    @NotEmpty(message = "题目ID不能为空")
    private String questionId;

    /** 用户选择的答案列表，单选题也用数组表示，例如 ["B"]。 */
    @NotEmpty(message = "选择答案不能为空")
    @Size(max = 8, message = "选择答案最多支持8项")
    private List<@Pattern(regexp = "^[A-Ha-h]$", message = "答案只能是A到H") String> selectedAnswer;

    /** 用户开始看题或开始作答的时间。 */
    @NotNull(message = "开始作答时间不能为空")
    private LocalDateTime startTime;

    /** 用户提交答案的时间。 */
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
