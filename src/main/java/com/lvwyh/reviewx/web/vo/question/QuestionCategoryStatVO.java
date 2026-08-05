package com.lvwyh.reviewx.web.vo.question;

import java.io.Serializable;

/** 题目分类数量统计。 */
public class QuestionCategoryStatVO implements Serializable {

    /** 分类名称，未填写分类的题目统一显示为“未分类”。 */
    private String category;
    /** 该分类下的启用题目数量。 */
    private Long count;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
