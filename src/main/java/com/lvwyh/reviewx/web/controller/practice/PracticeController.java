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
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    public ApiResponse<List<QuestionVO>> randomList(@Size(max = 64, message = "题目分类最多64个字符") @RequestParam(required = false) String questionType,
                                                    @Pattern(regexp = "^$|^\\d{4}$", message = "题目年份必须是4位数字") @RequestParam(required = false) String questionYear,
                                                    @Size(max = 255, message = "题目来源最多255个字符") @RequestParam(required = false) String questionSource,
                                                    @Pattern(regexp = "^$|^(all|done|undone)$", message = "随机范围不合法") @RequestParam(required = false) String randomScope,
                                                    @Min(value = 1, message = "取题数量必须大于0") @Max(value = 50, message = "取题数量不能超过50") @RequestParam(required = false) Integer size) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.randomList(userId, questionType, questionYear, questionSource, randomScope, size));
    }

    /**
     * 顺序分页取题。
     */
    @Operation(summary = "顺序刷题")
    @RequirePermission("practice:question")
    @GetMapping("/orderList")
    public ApiResponse<PageResult<QuestionVO>> orderList(@Size(max = 64, message = "题目分类最多64个字符") @RequestParam(required = false) String questionType,
                                                     @Pattern(regexp = "^$|^\\d{4}$", message = "题目年份必须是4位数字") @RequestParam(required = false) String questionYear,
                                                     @Size(max = 255, message = "题目来源最多255个字符") @RequestParam(required = false) String questionSource,
                                                     @Min(value = 1, message = "页码必须大于0") @RequestParam(required = false) Integer pageNum,
                                                     @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") @RequestParam(required = false) Integer pageSize) {
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
    public ApiResponse<List<QuestionVO>> wrongList(@Size(max = 64, message = "题目分类最多64个字符") @RequestParam(required = false) String questionType,
                                                   @Pattern(regexp = "^$|^\\d{4}$", message = "题目年份必须是4位数字") @RequestParam(required = false) String questionYear,
                                                   @Size(max = 255, message = "题目来源最多255个字符") @RequestParam(required = false) String questionSource,
                                                   @Min(value = 1, message = "取题数量必须大于0") @Max(value = 50, message = "取题数量不能超过50") @RequestParam(required = false) Integer size) {
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
