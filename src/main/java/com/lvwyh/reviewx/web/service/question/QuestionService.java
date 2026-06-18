package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;

import java.util.List;

public interface QuestionService {

    QuestionVO detail(String questionId);

    PageResult<QuestionVO> search(String keyword, String questionYear, String questionSource, Integer pageNum, Integer pageSize);

    List<QuestionVO> randomList(String questionYear, String questionSource, Integer size);

    PageResult<QuestionVO> orderList(String questionYear, String questionSource, Integer pageNum, Integer pageSize);

    List<QuestionVO> wrongList(Long userId, String questionYear, String questionSource, Integer size);
}
