package com.lvwyh.reviewx.web.vo.question;

import java.io.Serializable;

/**
 * 题目导入失败明细。
 *
 * 用于说明哪个文件中的第几道题失败，以及失败原因。
 */
public class QuestionImportFailureVO implements Serializable {

    /** zip 内部文件路径。 */
    private String fileName;
    /** 题目类型，来源于目录名。 */
    private String questionType;
    /** 文件内题目序号，从 1 开始。 */
    private Integer questionIndex;
    /** 题目 ID，缺失 ID 时为空。 */
    private String questionId;
    /** 失败原因。 */
    private String reason;

    public QuestionImportFailureVO() {
    }

    public QuestionImportFailureVO(String fileName, String questionType, Integer questionIndex, String questionId, String reason) {
        this.fileName = fileName;
        this.questionType = questionType;
        this.questionIndex = questionIndex;
        this.questionId = questionId;
        this.reason = reason;
    }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }
    public Integer getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(Integer questionIndex) { this.questionIndex = questionIndex; }
    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
