package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.QuestionAnalysis;
import org.apache.ibatis.annotations.Mapper;

/**
 * 题目解析 Mapper。
 *
 * 负责 question_analysis 表的解析维护。
 */
@Mapper
public interface QuestionAnalysisMapper {

    /** 新增或更新当前用户指定题目的解析。 */
    int upsert(QuestionAnalysis questionAnalysis);
}
