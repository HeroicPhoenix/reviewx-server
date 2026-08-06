package com.lvwyh.reviewx.web.service.question.impl;

import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.service.question.QuestionJsonTransferService;
import com.lvwyh.reviewx.web.service.question.QuestionTransferTaskService;
import com.lvwyh.reviewx.web.vo.question.QuestionJsonImportResultVO;
import com.lvwyh.reviewx.web.vo.question.QuestionTransferTaskVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/** 使用内存任务状态和临时文件实现可查询进度的导入导出。 */
@Service
public class QuestionTransferTaskServiceImpl implements QuestionTransferTaskService {

    private static final Logger log = LogManager.getLogger(QuestionTransferTaskServiceImpl.class);
    private static final long TASK_EXPIRE_MILLIS = 2 * 60 * 60 * 1000L;

    private final QuestionJsonTransferService transferService;
    private final Executor executor;
    private final Map<String, TaskState> tasks = new ConcurrentHashMap<String, TaskState>();

    public QuestionTransferTaskServiceImpl(QuestionJsonTransferService transferService,
                                           @Qualifier("questionTransferExecutor") Executor executor) {
        this.transferService = transferService;
        this.executor = executor;
    }

    @Override
    public QuestionTransferTaskVO startExport(Long userId, String scope) {
        cleanupExpiredTasks();
        TaskState task = createTask(userId, "export");
        tasks.put(task.taskId, task);
        submit(task, () -> runExport(task, scope));
        return snapshot(task);
    }

    @Override
    public QuestionTransferTaskVO startImport(Long userId, String scope, MultipartFile file) {
        cleanupExpiredTasks();
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要导入的 JSON 文件");
        }
        String originalName = file.getOriginalFilename();
        if (!StringUtils.hasText(originalName) || !originalName.toLowerCase(Locale.ROOT).endsWith(".json")) {
            throw new BusinessException(400, "仅支持上传 .json 文件");
        }
        TaskState task = createTask(userId, "import");
        try {
            task.tempFile = Files.createTempFile("reviewx-json-import-", ".json");
            try (InputStream input = file.getInputStream()) {
                Files.copy(input, task.tempFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            deleteFile(task.tempFile);
            throw new BusinessException(500, "保存上传文件失败：" + e.getMessage(), e);
        }
        tasks.put(task.taskId, task);
        submit(task, () -> runImport(task, scope, originalName));
        return snapshot(task);
    }

    @Override
    public QuestionTransferTaskVO status(Long userId, String taskId) {
        cleanupExpiredTasks();
        return snapshot(requireTask(userId, taskId));
    }

    @Override
    public Resource exportFile(Long userId, String taskId) {
        TaskState task = requireTask(userId, taskId);
        if (!"export".equals(task.taskType) || !"completed".equals(task.status)
                || task.tempFile == null || !Files.exists(task.tempFile)) {
            throw new BusinessException(409, "导出文件尚未生成");
        }
        return new FileSystemResource(task.tempFile.toFile());
    }

    private void runExport(TaskState task, String scope) {
        task.status = "processing";
        task.message = "正在生成 JSON 文件";
        try {
            task.tempFile = Files.createTempFile("reviewx-json-export-", ".json");
            try (OutputStream output = Files.newOutputStream(task.tempFile)) {
                transferService.exportJsonTo(task.userId, scope, output,
                        (processed, total) -> updateProgress(task, processed, total));
            }
            task.fileName = "reviewx-" + scope + "-" + LocalDate.now() + ".json";
            task.progress = 100;
            task.status = "completed";
            task.message = "导出文件已生成";
        } catch (Exception e) {
            fail(task, e);
        }
    }

    private void runImport(TaskState task, String scope, String originalName) {
        task.status = "processing";
        task.message = "正在导入题库数据";
        try (InputStream input = Files.newInputStream(task.tempFile)) {
            QuestionJsonImportResultVO result = transferService.importJson(task.userId, scope, input, originalName,
                    (processed, total) -> updateProgress(task, processed, total));
            task.importResult = result;
            task.progress = 100;
            task.status = "completed";
            task.message = "导入处理完成";
        } catch (Exception e) {
            fail(task, e);
        } finally {
            deleteFile(task.tempFile);
            task.tempFile = null;
        }
    }

    private void updateProgress(TaskState task, int processed, int total) {
        task.processedCount = processed;
        task.totalCount = total;
        task.progress = total <= 0 ? 100 : Math.min(99, (int) Math.floor(processed * 100D / total));
    }

    private void fail(TaskState task, Exception e) {
        task.status = "failed";
        if (e instanceof BusinessException && StringUtils.hasText(e.getMessage())) {
            task.message = e.getMessage();
        } else {
            // 非业务异常的数据库、文件路径和堆栈细节只写服务端日志，不能通过轮询接口暴露给前端。
            log.error("题库{}任务执行失败，taskId={}，userId={}", task.taskType, task.taskId, task.userId, e);
            task.message = "任务处理失败，请稍后重试";
        }
        deleteFile(task.tempFile);
        task.tempFile = null;
    }

    private void submit(TaskState task, Runnable action) {
        try {
            executor.execute(action);
        } catch (RuntimeException e) {
            tasks.remove(task.taskId);
            deleteFile(task.tempFile);
            throw new BusinessException(503, "当前导入导出任务较多，请稍后重试", e);
        }
    }

    private TaskState createTask(Long userId, String taskType) {
        if (userId == null) throw new BusinessException(401, "登录状态已失效");
        TaskState task = new TaskState();
        task.taskId = UUID.randomUUID().toString().replace("-", "");
        task.userId = userId;
        task.taskType = taskType;
        task.status = "queued";
        task.message = "任务已创建";
        task.createdAt = System.currentTimeMillis();
        return task;
    }

    private TaskState requireTask(Long userId, String taskId) {
        TaskState task = tasks.get(taskId);
        if (task == null || !task.userId.equals(userId)) {
            throw new BusinessException(404, "任务不存在或已过期");
        }
        return task;
    }

    private QuestionTransferTaskVO snapshot(TaskState task) {
        QuestionTransferTaskVO vo = new QuestionTransferTaskVO();
        vo.setTaskId(task.taskId);
        vo.setTaskType(task.taskType);
        vo.setStatus(task.status);
        vo.setProgress(task.progress);
        vo.setProcessedCount(task.processedCount);
        vo.setTotalCount(task.totalCount);
        vo.setMessage(task.message);
        vo.setFileName(task.fileName);
        vo.setImportResult(task.importResult);
        return vo;
    }

    private void cleanupExpiredTasks() {
        long expireBefore = System.currentTimeMillis() - TASK_EXPIRE_MILLIS;
        for (Map.Entry<String, TaskState> entry : tasks.entrySet()) {
            TaskState task = entry.getValue();
            if (task.createdAt < expireBefore && !"processing".equals(task.status)) {
                if (tasks.remove(entry.getKey(), task)) deleteFile(task.tempFile);
            }
        }
    }

    private void deleteFile(Path file) {
        if (file == null) return;
        try { Files.deleteIfExists(file); } catch (Exception ignored) { }
    }

    private static class TaskState {
        private String taskId;
        private Long userId;
        private String taskType;
        private volatile String status;
        private volatile int progress;
        private volatile int processedCount;
        private volatile int totalCount;
        private volatile String message;
        private volatile String fileName;
        private volatile QuestionJsonImportResultVO importResult;
        private volatile Path tempFile;
        private long createdAt;
    }
}
