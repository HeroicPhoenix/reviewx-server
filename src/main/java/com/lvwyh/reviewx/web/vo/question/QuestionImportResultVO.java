package com.lvwyh.reviewx.web.vo.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 题目导入结果。
 *
 * 汇总导入文件数、题目数、成功数和失败明细。
 */
public class QuestionImportResultVO implements Serializable {

    /** 实际读取的 JSON 文件总数，不包含 logs 目录。 */
    private int totalFileCount;
    /** JSON 中识别到的题目总数。 */
    private int totalQuestionCount;
    /** 成功导入题目数。 */
    private int successQuestionCount;
    /** 导入失败题目数。 */
    private int failedQuestionCount;
    /** 缺少 ID 的题目数。 */
    private int missingIdQuestionCount;
    /** 文件级或题目级失败明细。 */
    private List<QuestionImportFailureVO> failures = new ArrayList<QuestionImportFailureVO>();
    /** 缺少 ID 的题目明细。 */
    private List<QuestionImportFailureVO> missingIdQuestions = new ArrayList<QuestionImportFailureVO>();

    public int getTotalFileCount() { return totalFileCount; }
    public void setTotalFileCount(int totalFileCount) { this.totalFileCount = totalFileCount; }
    public int getTotalQuestionCount() { return totalQuestionCount; }
    public void setTotalQuestionCount(int totalQuestionCount) { this.totalQuestionCount = totalQuestionCount; }
    public int getSuccessQuestionCount() { return successQuestionCount; }
    public void setSuccessQuestionCount(int successQuestionCount) { this.successQuestionCount = successQuestionCount; }
    public int getFailedQuestionCount() { return failedQuestionCount; }
    public void setFailedQuestionCount(int failedQuestionCount) { this.failedQuestionCount = failedQuestionCount; }
    public int getMissingIdQuestionCount() { return missingIdQuestionCount; }
    public void setMissingIdQuestionCount(int missingIdQuestionCount) { this.missingIdQuestionCount = missingIdQuestionCount; }
    public List<QuestionImportFailureVO> getFailures() { return failures; }
    public void setFailures(List<QuestionImportFailureVO> failures) { this.failures = failures; }
    public List<QuestionImportFailureVO> getMissingIdQuestions() { return missingIdQuestions; }
    public void setMissingIdQuestions(List<QuestionImportFailureVO> missingIdQuestions) { this.missingIdQuestions = missingIdQuestions; }

    public void increaseTotalFileCount() { this.totalFileCount++; }
    public void increaseTotalQuestionCount() { this.totalQuestionCount++; }
    public void increaseSuccessQuestionCount() { this.successQuestionCount++; }

    public void addFailure(QuestionImportFailureVO failure) {
        this.failedQuestionCount++;
        this.failures.add(failure);
    }

    public void addMissingIdQuestion(QuestionImportFailureVO failure) {
        this.missingIdQuestionCount++;
        addFailure(failure);
        this.missingIdQuestions.add(failure);
    }
}
