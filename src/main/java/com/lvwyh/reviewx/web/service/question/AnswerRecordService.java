package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.ao.practice.SubmitAnswerAO;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordStatVO;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordVO;
import com.lvwyh.reviewx.web.vo.practice.SubmitAnswerVO;

import java.time.LocalDateTime;

public interface AnswerRecordService {

    SubmitAnswerVO submitAnswer(Long userId, SubmitAnswerAO ao);

    PageResult<AnswerRecordVO> listPage(Long userId, String questionId, Integer isCorrect, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize);

    AnswerRecordStatVO stat(Long userId);
}
