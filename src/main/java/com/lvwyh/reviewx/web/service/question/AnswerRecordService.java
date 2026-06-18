package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.ao.practice.SubmitAnswerAO;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordStatVO;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordVO;
import com.lvwyh.reviewx.web.vo.practice.SubmitAnswerVO;

import java.time.LocalDateTime;

/**
 * 答题记录服务接口。
 *
 * 负责提交答案、查询历史记录和统计当前用户刷题表现。
 */
public interface AnswerRecordService {

    /** 提交答案并保存作答记录。 */
    SubmitAnswerVO submitAnswer(Long userId, SubmitAnswerAO ao);

    /** 分页查询当前用户答题记录。 */
    PageResult<AnswerRecordVO> listPage(Long userId, String questionId, Integer isCorrect, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize);

    /** 统计当前用户答题情况。 */
    AnswerRecordStatVO stat(Long userId);
}
