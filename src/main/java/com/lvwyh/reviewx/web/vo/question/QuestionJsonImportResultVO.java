package com.lvwyh.reviewx.web.vo.question;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** JSON 题库导入结果。 */
public class QuestionJsonImportResultVO implements Serializable {

    private int totalCount;
    private int successCount;
    private int failureCount;
    private List<String> failures = new ArrayList<String>();

    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }
    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
    public List<String> getFailures() { return failures; }
    public void setFailures(List<String> failures) { this.failures = failures; }

    public void increaseTotalCount() { totalCount++; }
    public void increaseSuccessCount() { successCount++; }

    public void addFailure(String failure) {
        failureCount++;
        failures.add(failure);
    }
}
