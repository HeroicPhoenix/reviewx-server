package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.QuestionAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 题目解析 Mapper。
 *
 * 负责 question_analysis 表的解析维护。
 */
@Mapper
public interface QuestionAnalysisMapper {

    /** 新增或更新当前用户指定题目的解析。 */
    int upsert(QuestionAnalysis questionAnalysis);

    /** 删除当前用户的全部题目解析，清空题库时避免遗留孤立数据。 */
    int deleteByUserId(@Param("userId") Long userId);
}
