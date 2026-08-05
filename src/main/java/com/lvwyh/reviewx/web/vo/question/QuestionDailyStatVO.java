package com.lvwyh.reviewx.web.vo.question;

import java.io.Serializable;

/** 按加入日期统计的题目新增数量。 */
public class QuestionDailyStatVO implements Serializable {

    /** 加入日期，格式为 YYYY-MM-DD。 */
    private String date;
    /** 当日新增的启用题目数量。 */
    private Long count;

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
