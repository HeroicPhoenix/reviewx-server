package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.ao.question.QuestionAnalysisUpdateAO;
import com.lvwyh.reviewx.web.ao.question.QuestionUpdateAO;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;

import java.util.List;

/**
 * 题目查询服务接口。
 *
 * 提供题目查看、搜索、刷题取题和题目维护能力。
 */
public interface QuestionService {

    /** 查看题目详情，允许返回正确答案。 */
    QuestionVO detail(Long userId, String questionId);

    /** 搜索题目，返回分页结果。 */
    PageResult<QuestionVO> search(Long userId, String keyword, String questionType, String questionYear, String questionSource, String questionJoinDate, Integer pageNum, Integer pageSize);

    /** 编辑当前用户自己的题目。 */
    QuestionVO update(Long userId, QuestionUpdateAO ao);

    /** 编辑当前用户自己的题目解析。 */
    QuestionVO updateAnalysis(Long userId, QuestionAnalysisUpdateAO ao);

    /** 查询启用题目的题型下拉列表。 */
    List<String> questionTypes(Long userId);

    /** 查询启用题目的年份下拉列表。 */
    List<String> questionYears(Long userId);

    /** 查询启用题目的来源下拉列表。 */
    List<String> questionSources(Long userId);

    /** 随机抽题，不返回正确答案。 */
    List<QuestionVO> randomList(Long userId, String questionType, String questionYear, String questionSource, String randomScope, Integer size);

    /** 顺序分页取题，不返回正确答案。 */
    PageResult<QuestionVO> orderList(Long userId, String questionType, String questionYear, String questionSource, Integer pageNum, Integer pageSize);

    /** 从当前用户错题中随机取题，不返回正确答案。 */
    List<QuestionVO> wrongList(Long userId, String questionType, String questionYear, String questionSource, Integer size);
}
