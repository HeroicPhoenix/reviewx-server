package com.lvwyh.reviewx.web.controller.question;

import com.lvwyh.reviewx.web.ao.question.QuestionUpdateAO;
import com.lvwyh.reviewx.web.ao.question.QuestionAnalysisUpdateAO;
import com.lvwyh.reviewx.web.common.response.ApiResponse;
import com.lvwyh.reviewx.web.common.util.PageResult;
import com.lvwyh.reviewx.web.security.LoginUserContext;
import com.lvwyh.reviewx.web.security.RequirePermission;
import com.lvwyh.reviewx.web.service.question.QuestionImportService;
import com.lvwyh.reviewx.web.service.question.QuestionJsonTransferService;
import com.lvwyh.reviewx.web.service.question.QuestionService;
import com.lvwyh.reviewx.web.service.question.QuestionTransferTaskService;
import com.lvwyh.reviewx.web.vo.question.QuestionImportResultVO;
import com.lvwyh.reviewx.web.vo.question.QuestionJsonImportResultVO;
import com.lvwyh.reviewx.web.vo.question.QuestionTransferTaskVO;
import com.lvwyh.reviewx.web.vo.question.QuestionVO;
import com.lvwyh.reviewx.web.vo.question.QuestionOverviewStatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.annotation.Validated;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 题目查询控制器。
 *
 * 提供题目查看、搜索、导入和编辑能力。
 */
@Tag(name = "题目管理")
@Validated
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    private static final Logger log = LogManager.getLogger(QuestionController.class);

    private final QuestionService questionService;
    private final QuestionImportService questionImportService;
    private final QuestionJsonTransferService questionJsonTransferService;
    private final QuestionTransferTaskService questionTransferTaskService;

    public QuestionController(QuestionService questionService,
                              QuestionImportService questionImportService,
                              QuestionJsonTransferService questionJsonTransferService,
                              QuestionTransferTaskService questionTransferTaskService) {
        this.questionService = questionService;
        this.questionImportService = questionImportService;
        this.questionJsonTransferService = questionJsonTransferService;
        this.questionTransferTaskService = questionTransferTaskService;
    }

    /**
     * 查看题目详情。
     *
     * 该接口属于查看模式，会返回正确答案和题目图片。
     */
    @Operation(summary = "查看题目详情")
    @RequirePermission("question:view")
    @GetMapping("/detail")
    public ApiResponse<QuestionVO> detail(@NotBlank(message = "题目ID不能为空") @RequestParam String questionId) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Question detail request: userId={}, questionId={}", userId, questionId);
        return ApiResponse.success("查询成功", questionService.detail(userId, questionId));
    }

    /**
     * 搜索题目。
     *
     * 支持关键词、年份和来源过滤；列表不返回图片 Base64。
     */
    @Operation(summary = "搜索题目")
    @RequirePermission("question:search")
    @GetMapping("/search")
    public ApiResponse<PageResult<QuestionVO>> search(@Size(max = 255, message = "关键词最多255个字符") @RequestParam(required = false) String keyword,
                                                  @Size(max = 64, message = "题目分类最多64个字符") @RequestParam(required = false) String questionType,
                                                  @Pattern(regexp = "^$|^\\d{4}$", message = "题目年份必须是4位数字") @RequestParam(required = false) String questionYear,
                                                  @Size(max = 255, message = "题目来源最多255个字符") @RequestParam(required = false) String questionSource,
                                                  @Pattern(regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$", message = "题目加入日期格式必须为YYYY-MM-DD") @RequestParam(required = false) String questionJoinDate,
                                                  @Min(value = 1, message = "页码必须大于0") @RequestParam(required = false) Integer pageNum,
                                                  @Min(value = 1, message = "每页数量必须大于0") @Max(value = 100, message = "每页数量不能超过100") @RequestParam(required = false) Integer pageSize) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Question search request: userId={}, keyword={}, questionType={}, questionYear={}, questionSource={}, questionJoinDate={}", userId, keyword, questionType, questionYear, questionSource, questionJoinDate);
        return ApiResponse.success("查询成功", questionService.search(userId, keyword, questionType, questionYear, questionSource, questionJoinDate, pageNum, pageSize));
    }

    /**
     * 编辑当前用户自己的题目。
     */
    @Operation(summary = "编辑题目")
    @RequirePermission("question:update")
    @PostMapping("/update")
    public ApiResponse<QuestionVO> update(@Valid @RequestBody QuestionUpdateAO ao) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Question update request: userId={}, questionId={}", userId, ao.getQuestionId());
        return ApiResponse.success("保存成功", questionService.update(userId, ao));
    }

    /**
     * 编辑当前用户自己的题目解析。
     */
    @Operation(summary = "编辑题目解析")
    @RequirePermission("question:update")
    @PostMapping("/updateAnalysis")
    public ApiResponse<QuestionVO> updateAnalysis(@Valid @RequestBody QuestionAnalysisUpdateAO ao) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Question analysis update request: userId={}, questionId={}", userId, ao.getQuestionId());
        return ApiResponse.success("保存成功", questionService.updateAnalysis(userId, ao));
    }

    /**
     * 查询题型下拉选项。
     */
    @Operation(summary = "按题型分组查询")
    @RequirePermission("question:search")
    @GetMapping("/typeList")
    public ApiResponse<List<String>> typeList() {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.questionTypes(userId));
    }

    /**
     * 查询年份下拉选项。
     */
    @Operation(summary = "按年份分组查询")
    @RequirePermission("question:search")
    @GetMapping("/yearList")
    public ApiResponse<List<String>> yearList() {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.questionYears(userId));
    }

    /**
     * 查询来源下拉选项。
     */
    @Operation(summary = "按来源分组查询")
    @RequirePermission("question:search")
    @GetMapping("/sourceList")
    public ApiResponse<List<String>> sourceList() {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.questionSources(userId));
    }

    /**
     * 查询加入日期下拉选项。
     */
    @Operation(summary = "按加入日期分组查询")
    @RequirePermission("question:search")
    @GetMapping("/joinDateList")
    public ApiResponse<List<String>> joinDateList() {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.questionJoinDates(userId));
    }

    /**
     * 查询概览页题库统计。
     */
    @Operation(summary = "查询题库概览统计")
    @RequirePermission("question:search")
    @GetMapping("/overviewStat")
    public ApiResponse<QuestionOverviewStatVO> overviewStat(
            @Pattern(regexp = "all|analyzed|unanalyzed", message = "解析范围参数错误")
            @RequestParam(defaultValue = "all") String analysisScope) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("查询成功", questionService.overviewStat(userId, analysisScope));
    }

    /**
     * 从上传 zip 导入题目。
     *
     * 导入时会跳过 logs 目录，并将其它一级目录名作为题目类型。
     */
    @Operation(summary = "上传zip导入题目")
    @RequirePermission("question:import")
    @PostMapping("/importFromDocsZip")
    public ApiResponse<QuestionImportResultVO> importFromDocsZip(@RequestParam("file") MultipartFile file,
                                                                 @RequestParam(value = "clearBeforeImport", defaultValue = "false") Boolean clearBeforeImport) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Question import zip upload request: userId={}, filename={}, size={}, clearBeforeImport={}", userId, file.getOriginalFilename(), file.getSize(), clearBeforeImport);
        return ApiResponse.success("导入完成", questionImportService.importFromZip(userId, file, Boolean.TRUE.equals(clearBeforeImport)));
    }

    /**
     * 将当前用户的题库导出为 JSON 数据。
     */
    @Operation(summary = "导出题库 JSON")
    @RequirePermission("question:view")
    @GetMapping("/exportJson")
    public ApiResponse<Void> exportJson(@RequestParam String scope) {
        throw new com.lvwyh.reviewx.web.common.exception.BusinessException(410,
                "同步导出接口已停用，请使用 exportJsonTask 创建流式导出任务");
    }

    /**
     * 从 JSON 文件向当前用户题库导入题目或解析。
     */
    @Operation(summary = "导入题库 JSON")
    @RequirePermission("question:import")
    @PostMapping("/importJson")
    public ApiResponse<QuestionJsonImportResultVO> importJson(@RequestParam("file") MultipartFile file,
                                                               @RequestParam String scope) {
        Long userId = LoginUserContext.require().getUserId();
        log.info("Question JSON import request: userId={}, filename={}, size={}, scope={}",
                userId, file.getOriginalFilename(), file.getSize(), scope);
        return ApiResponse.success("导入完成", questionJsonTransferService.importJson(userId, scope, file));
    }

    /** 创建带进度的 JSON 导出任务。 */
    @Operation(summary = "创建题库 JSON 导出任务")
    @RequirePermission("question:view")
    @PostMapping("/exportJsonTask")
    public ApiResponse<QuestionTransferTaskVO> startExportJsonTask(@RequestParam String scope) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("导出任务已创建", questionTransferTaskService.startExport(userId, scope));
    }

    /** 上传 JSON 并创建带进度的导入任务。 */
    @Operation(summary = "创建题库 JSON 导入任务")
    @RequirePermission("question:import")
    @PostMapping("/importJsonTask")
    public ApiResponse<QuestionTransferTaskVO> startImportJsonTask(@RequestParam("file") MultipartFile file,
                                                                    @RequestParam String scope) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success("导入任务已创建", questionTransferTaskService.startImport(userId, scope, file));
    }

    /** 查询当前用户导入导出任务的真实进度。 */
    @Operation(summary = "查询题库 JSON 任务进度")
    @RequirePermission("question:view")
    @GetMapping("/jsonTransferTask")
    public ApiResponse<QuestionTransferTaskVO> jsonTransferTask(@RequestParam String taskId) {
        Long userId = LoginUserContext.require().getUserId();
        return ApiResponse.success(questionTransferTaskService.status(userId, taskId));
    }

    /** 下载已完成的 JSON 导出文件。 */
    @Operation(summary = "下载题库 JSON 导出文件")
    @RequirePermission("question:view")
    @GetMapping("/downloadJson")
    public ResponseEntity<Resource> downloadJson(@RequestParam String taskId) throws java.io.IOException {
        Long userId = LoginUserContext.require().getUserId();
        QuestionTransferTaskVO task = questionTransferTaskService.status(userId, taskId);
        Resource resource = questionTransferTaskService.exportFile(userId, taskId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .contentLength(resource.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + task.getFileName() + "\"")
                .body(resource);
    }
}
