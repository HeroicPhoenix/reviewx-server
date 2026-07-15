package com.lvwyh.reviewx.web.vo.question;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/** 题库 JSON 导入导出任务状态。 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionTransferTaskVO implements Serializable {

    private String taskId;
    private String taskType;
    private String status;
    private int progress;
    private int processedCount;
    private int totalCount;
    private String message;
    private String fileName;
    private QuestionJsonImportResultVO importResult;

    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public int getProcessedCount() { return processedCount; }
    public void setProcessedCount(int processedCount) { this.processedCount = processedCount; }
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public QuestionJsonImportResultVO getImportResult() { return importResult; }
    public void setImportResult(QuestionJsonImportResultVO importResult) { this.importResult = importResult; }
}
