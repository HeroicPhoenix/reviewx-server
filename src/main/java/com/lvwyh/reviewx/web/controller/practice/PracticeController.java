package com.lvwyh.reviewx.web.controller.practice;

import com.lvwyh.reviewx.web.ao.practice.SubmitAnswerAO;
import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.security.LoginUserContext;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.question.AnswerRecordService;
import com.lvwyh.reviewx.web.service.question.QuestionService;
import com.lvwyh.reviewx.web.vo.practice.SubmitAnswerVO;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 刷题控制器。
 *
 * 负责三种取题方式和提交答案；取题接口不返回正确答案。
 */
@Tag(name = "刷题")
@Validated
@RestController
@RequestMapping("/api/practice")
public class PracticeController {

    private static final Logger log = LogManager.getLogger(PracticeController.class);

    private final QuestionService questionService;
    private final AnswerRecordService answerRecordService;

    public PracticeController(QuestionService questionService, AnswerRecordService answerRecordService) {
        this.questionService = questionService;
        this.answerRecordService = answerRecordService;
    }

    /**
     * 随机取题。
     */
    @Operation(summary = "随机刷题")
    @RequirePermission("practice:question")
    @GetMapping("/randomList")
    public ApiResponse<List<QuestionVO>> randomList(@RequestParam(required = false) String questionType,
                                                    @RequestParam(required = false) String questionYear,
                                                    @RequestParam(required = false) String questionSource,
                                                    @RequestParam(required = false) Integer size) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.randomList(userId, questionType, questionYear, questionSource, size));
    }

    /**
     * 顺序分页取题。
     */
    @Operation(summary = "顺序刷题")
    @RequirePermission("practice:question")
    @GetMapping("/orderList")
    public ApiResponse<PageResult<QuestionVO>> orderList(@RequestParam(required = false) String questionType,
                                                     @RequestParam(required = false) String questionYear,
                                                     @RequestParam(required = false) String questionSource,
                                                     @RequestParam(required = false) Integer pageNum,
                                                     @RequestParam(required = false) Integer pageSize) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.orderList(userId, questionType, questionYear, questionSource, pageNum, pageSize));
    }

    /**
     * 错题随机取题。
     *
     * 只查询当前登录用户历史答错过的题目。
     */
    @Operation(summary = "错题刷题")
    @RequirePermission("practice:question")
    @GetMapping("/wrongList")
    public ApiResponse<List<QuestionVO>> wrongList(@RequestParam(required = false) String questionType,
                                                   @RequestParam(required = false) String questionYear,
                                                   @RequestParam(required = false) String questionSource,
                                                   @RequestParam(required = false) Integer size) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.wrongList(userId, questionType, questionYear, questionSource, size));
    }

    /**
     * 提交答案。
     *
     * 服务端读取正确答案完成判题，并保存 ANSWER_RECORD。
     */
    @Operation(summary = "提交答案")
    @RequirePermission("practice:submit")
    @PostMapping("/submitAnswer")
    public ApiResponse<SubmitAnswerVO> submitAnswer(@Valid @RequestBody SubmitAnswerAO ao) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Submit answer request: userId={}, questionId={}", userId, ao.getQuestionId());
        return ApiResponse.success("提交成功", answerRecordService.submitAnswer(userId, ao));
    }
}
