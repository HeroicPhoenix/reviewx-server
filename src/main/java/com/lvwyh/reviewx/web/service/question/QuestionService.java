package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;

import java.util.List;

/**
 * 题目查询服务接口。
 *
 * 第一版只提供题目查看和刷题取题能力，不提供题目录入和维护能力。
 */
public interface QuestionService {

    /** 查看题目详情，允许返回正确答案。 */
    QuestionVO detail(Long userId, String questionId);

    /** 搜索题目，返回分页结果。 */
    PageResult<QuestionVO> search(Long userId, String keyword, String questionType, String questionYear, String questionSource, Integer pageNum, Integer pageSize);

    /** 查询启用题目的题型下拉列表。 */
    List<String> questionTypes(Long userId);

    /** 随机抽题，不返回正确答案。 */
    List<QuestionVO> randomList(Long userId, String questionType, String questionYear, String questionSource, Integer size);

    /** 顺序分页取题，不返回正确答案。 */
    PageResult<QuestionVO> orderList(Long userId, String questionType, String questionYear, String questionSource, Integer pageNum, Integer pageSize);

    /** 从当前用户错题中随机取题，不返回正确答案。 */
    List<QuestionVO> wrongList(Long userId, String questionType, String questionYear, String questionSource, Integer size);
}
