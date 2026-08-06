package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.QuestionAnalysis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目解析 Mapper。
 *
 * 负责 question_analysis 表的解析维护。
 */
@Mapper
public interface QuestionAnalysisMapper {

    /**
     * 仅按 (USER_ID, QUESTION_ID) 精确新增或覆盖同一条解析。
     *
     * 本 Mapper 禁止提供删除解析的方法；清空或重新导入题目也必须保留解析记录。
     */
    int upsert(QuestionAnalysis questionAnalysis);

    /** 统计当前用户全部保留的解析，包括对应题目暂不存在的解析。 */
    long countByUserId(@Param("userId") Long userId);

    /** 分页查询当前用户全部保留的解析，用于完整备份。 */
    List<QuestionAnalysis> selectPageByUserId(@Param("userId") Long userId,
                                              @Param("offset") int offset,
                                              @Param("pageSize") int pageSize);
}
