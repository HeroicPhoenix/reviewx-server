package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.vo.question.QuestionCategoryStatVO;
import com.lvwyh.reviewx.web.vo.question.QuestionDailyStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 题目 Mapper。
 *
 * 负责 question 表查询和题目维护，题目查询会左关联解析表返回解析字段。
 */
@Mapper
public interface QuestionMapper {

    /** 查询联动筛选的可选值。 */
    List<String> selectPracticeQuestionTypes(@Param("userId") Long userId, @Param("questionType") String questionType, @Param("questionYear") String questionYear, @Param("questionSource") String questionSource, @Param("questionJoinDate") String questionJoinDate, @Param("analysisScope") String analysisScope);

    /** 查询联动筛选的可选值。 */
    List<String> selectPracticeQuestionYears(@Param("userId") Long userId, @Param("questionType") String questionType, @Param("questionYear") String questionYear, @Param("questionSource") String questionSource, @Param("questionJoinDate") String questionJoinDate, @Param("analysisScope") String analysisScope);

    /** 查询联动筛选的可选值。 */
    List<String> selectPracticeQuestionSources(@Param("userId") Long userId, @Param("questionType") String questionType, @Param("questionYear") String questionYear, @Param("questionSource") String questionSource, @Param("questionJoinDate") String questionJoinDate, @Param("analysisScope") String analysisScope);

    /** 查询联动筛选的可选值。 */
    List<String> selectPracticeQuestionJoinDates(@Param("userId") Long userId, @Param("questionType") String questionType, @Param("questionYear") String questionYear, @Param("questionSource") String questionSource, @Param("questionJoinDate") String questionJoinDate, @Param("analysisScope") String analysisScope);

    /** 新增或更新题目，导入题库时按 questionId 幂等写入。 */
    int upsert(Question question);

    /** 删除当前用户题库中的全部题目。 */
    int deleteByUserId(@Param("userId") Long userId);

    /** 按题目 ID 更新当前用户自己的题目。 */
    int updateByIdAndUserId(Question question);

    /** 按题目 ID 查询启用状态的题目。 */
    Question selectById(@Param("userId") Long userId, @Param("questionId") String questionId);

    /** 刷题时按需查询当前题目的图片，避免取题列表批量加载 Base64。 */
    Question selectImagesById(@Param("userId") Long userId, @Param("questionId") String questionId);

    /** 统计当前用户可导出的启用题目数量。 */
    long countForExport(@Param("userId") Long userId);

    /** 分页查询导出数据，限制单次加载的 Base64 大字段数量。 */
    List<Question> selectForExportPage(@Param("userId") Long userId,
                                       @Param("offset") long offset,
                                       @Param("pageSize") int pageSize);

    /** 按关键字、年份、来源分页搜索题目。 */
    List<Question> search(@Param("userId") Long userId,
                          @Param("keyword") String keyword,
                          @Param("questionType") String questionType,
                          @Param("questionYear") String questionYear,
                          @Param("questionSource") String questionSource,
                          @Param("questionJoinDate") String questionJoinDate,
                          @Param("offset") long offset,
                          @Param("pageSize") int pageSize);

    /** 统计搜索条件下的题目总数。 */
    long countSearch(@Param("userId") Long userId,
                     @Param("keyword") String keyword,
                     @Param("questionType") String questionType,
                     @Param("questionYear") String questionYear,
                     @Param("questionSource") String questionSource,
                     @Param("questionJoinDate") String questionJoinDate);

    /** 按题目类型分组查询启用题目的题型列表。 */
    List<String> selectQuestionTypes(@Param("userId") Long userId);

    /** 按年份分组查询启用题目的年份列表。 */
    List<String> selectQuestionYears(@Param("userId") Long userId);

    /** 按来源分组查询启用题目的来源列表。 */
    List<String> selectQuestionSources(@Param("userId") Long userId);

    /** 按加入日期分组查询启用题目的加入日期列表。 */
    List<String> selectQuestionJoinDates(@Param("userId") Long userId);

    /** 按数据库分类字段统计当前用户的启用题目数量。 */
    List<QuestionCategoryStatVO> selectCategoryCounts(@Param("userId") Long userId,
                                                      @Param("analysisScope") String analysisScope);

    /** 按加入日期统计当前用户每日新增的启用题目数量。 */
    List<QuestionDailyStatVO> selectDailyCounts(@Param("userId") Long userId);

    /** 随机抽取题目。 */
    List<Question> selectRandom(@Param("userId") Long userId,
                                @Param("questionType") String questionType,
                                @Param("questionYear") String questionYear,
                                @Param("questionSource") String questionSource,
                                @Param("questionJoinDate") String questionJoinDate,
                                @Param("randomScope") String randomScope,
                                @Param("analysisScope") String analysisScope,
                                @Param("size") int size);

    /** 按题目 ID 顺序分页查询题目。 */
    List<Question> selectOrder(@Param("userId") Long userId,
                               @Param("questionType") String questionType,
                               @Param("questionYear") String questionYear,
                               @Param("questionSource") String questionSource,
                               @Param("questionJoinDate") String questionJoinDate,
                               @Param("analysisScope") String analysisScope,
                               @Param("orderDirection") String orderDirection,
                               @Param("offset") long offset,
                               @Param("pageSize") int pageSize);

    /** 统计顺序刷题筛选条件下的题目总数。 */
    long countOrder(@Param("userId") Long userId,
                    @Param("questionType") String questionType,
                    @Param("questionYear") String questionYear,
                    @Param("questionSource") String questionSource,
                    @Param("questionJoinDate") String questionJoinDate,
                    @Param("analysisScope") String analysisScope);

    /** 从当前用户历史错题中随机抽取题目。 */
    List<Question> selectWrong(@Param("userId") Long userId,
                               @Param("questionType") String questionType,
                               @Param("questionYear") String questionYear,
                               @Param("questionSource") String questionSource,
                               @Param("questionJoinDate") String questionJoinDate,
                               @Param("size") int size);
}
