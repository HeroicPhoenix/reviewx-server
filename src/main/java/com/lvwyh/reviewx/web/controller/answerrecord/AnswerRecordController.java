package com.lvwyh.reviewx.web.controller.answerrecord;

import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.security.LoginUserContext;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.question.AnswerRecordService;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordStatVO;
import com.lvwyh.reviewx.web.vo.practice.AnswerRecordVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "答题记录")
@Validated
@RestController
@RequestMapping("/api/answerRecord")
public class AnswerRecordController {

    private final AnswerRecordService answerRecordService;

    public AnswerRecordController(AnswerRecordService answerRecordService) {
        this.answerRecordService = answerRecordService;
    }

    @Operation(summary = "分页查询答题记录")
    @RequirePermission("answer-record:view")
    @GetMapping("/listPage")
    public ApiResponse<PageResult<AnswerRecordVO>> listPage(@RequestParam(required = false) String questionId,
                                                        @RequestParam(required = false) Integer isCorrect,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                                                        @RequestParam(required = false) Integer pageNum,
                                                        @RequestParam(required = false) Integer pageSize) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", answerRecordService.listPage(userId, questionId, isCorrect, startTime, endTime, pageNum, pageSize));
    }

    @Operation(summary = "查询答题统计")
    @RequirePermission("answer-record:view")
    @GetMapping("/stat")
    public ApiResponse<AnswerRecordStatVO> stat() {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", answerRecordService.stat(userId));
    }
}
