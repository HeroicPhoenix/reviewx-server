package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目 Mapper。
 *
 * 负责 question 表查询和题目维护。
 */
@Mapper
public interface QuestionMapper {

    /** 新增或更新题目，导入题库时按 questionId 幂等写入。 */
    int upsert(Question question);

    /** 删除当前用户题库中的全部题目。 */
    int deleteByUserId(@Param("userId") Long userId);

    /** 按题目 ID 更新当前用户自己的题目。 */
    int updateByIdAndUserId(Question question);

    /** 按题目 ID 更新当前用户自己的题目解析。 */
    int updateAnalysisByIdAndUserId(Question question);

    /** 按题目 ID 查询启用状态的题目。 */
    Question selectById(@Param("userId") Long userId, @Param("questionId") String questionId);

    /** 按关键字、年份、来源分页搜索题目。 */
    List<Question> search(@Param("userId") Long userId,
                          @Param("keyword") String keyword,
                          @Param("questionType") String questionType,
                          @Param("questionYear") String questionYear,
                          @Param("questionSource") String questionSource,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);

    /** 统计搜索条件下的题目总数。 */
    long countSearch(@Param("userId") Long userId,
                     @Param("keyword") String keyword,
                     @Param("questionType") String questionType,
                     @Param("questionYear") String questionYear,
                     @Param("questionSource") String questionSource);

    /** 按题目类型分组查询启用题目的题型列表。 */
    List<String> selectQuestionTypes(@Param("userId") Long userId);

    /** 按年份分组查询启用题目的年份列表。 */
    List<String> selectQuestionYears(@Param("userId") Long userId);

    /** 按来源分组查询启用题目的来源列表。 */
    List<String> selectQuestionSources(@Param("userId") Long userId);

    /** 随机抽取题目。 */
    List<Question> selectRandom(@Param("userId") Long userId,
                                @Param("questionType") String questionType,
                                @Param("questionYear") String questionYear,
                                @Param("questionSource") String questionSource,
                                @Param("randomScope") String randomScope,
                                @Param("size") int size);

    /** 按题目 ID 顺序分页查询题目。 */
    List<Question> selectOrder(@Param("userId") Long userId,
                               @Param("questionType") String questionType,
                               @Param("questionYear") String questionYear,
                               @Param("questionSource") String questionSource,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    /** 从当前用户历史错题中随机抽取题目。 */
    List<Question> selectWrong(@Param("userId") Long userId,
                               @Param("questionType") String questionType,
                               @Param("questionYear") String questionYear,
                               @Param("questionSource") String questionSource,
                               @Param("size") int size);
}
