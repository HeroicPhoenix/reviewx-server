package com.lvwyh.reviewx.web.controller.question;

import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.question.QuestionService;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@Tag(name = "题目管理")
@Validated
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private static final Logger log = LogManager.getLogger(QuestionController.class);

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Operation(summary = "查看题目详情")
    @RequirePermission("question:view")
    @GetMapping("/detail")
    public ApiResponse<QuestionVO> detail(@NotBlank(message = "题目ID不能为空") @RequestParam String questionId) {
        log.info("Question detail request: questionId={}", questionId);
        return ApiResponse.success("查询成功", questionService.detail(questionId));
    }

    @Operation(summary = "搜索题目")
    @RequirePermission("question:search")
    @GetMapping("/search")
    public ApiResponse<PageResult<QuestionVO>> search(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String questionYear,
                                                  @RequestParam(required = false) String questionSource,
                                                  @RequestParam(required = false) Integer pageNum,
                                                  @RequestParam(required = false) Integer pageSize) {
        log.info("Question search request: keyword={}, questionYear={}, questionSource={}", keyword, questionYear, questionSource);
        return ApiResponse.success("查询成功", questionService.search(keyword, questionYear, questionSource, pageNum, pageSize));
    }
}
