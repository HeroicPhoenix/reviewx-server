package com.lvwyh.reviewx.web.vo.question;

import java.io.Serializable;
import java.util.List;

/** 概览页题库统计。 */
public class QuestionOverviewStatVO implements Serializable {

    /** 当前用户的启用题目总数。 */
    private Long totalCount;
    /** 按数据库分类字段聚合的题目数量。 */
    private List<QuestionCategoryStatVO> categoryCounts;
    /** 按题目加入日期聚合的每日新增数量。 */
    private List<QuestionDailyStatVO> dailyCounts;

    public Long getTotalCount() { return totalCount; }
    public void setTotalCount(Long totalCount) { this.totalCount = totalCount; }
    public List<QuestionCategoryStatVO> getCategoryCounts() { return categoryCounts; }
    public void setCategoryCounts(List<QuestionCategoryStatVO> categoryCounts) { this.categoryCounts = categoryCounts; }
    public List<QuestionDailyStatVO> getDailyCounts() { return dailyCounts; }
    public void setDailyCounts(List<QuestionDailyStatVO> dailyCounts) { this.dailyCounts = dailyCounts; }
}
