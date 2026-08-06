package com.lvwyh.reviewx.web.service.question.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.QuestionImportService;
import com.lvwyh.reviewx.web.vo.question.QuestionImportFailureVO;
import com.lvwyh.reviewx.web.vo.question.QuestionImportResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 题目导入服务实现。
 *
 * 从上传 zip 文件中读取各题型目录下的 JSON 文件，logs 目录会被跳过。
 */
@Service
public class QuestionImportServiceImpl implements QuestionImportService {

    private static final String LOGS_DIR_NAME = "logs";
    private static final DateTimeFormatter JOIN_DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
    private static final long MAX_ENTRY_UNCOMPRESSED_BYTES = 50L * 1024L * 1024L;
    private static final long MAX_TOTAL_UNCOMPRESSED_BYTES = 200L * 1024L * 1024L;
    private static final long MAX_COMPRESSION_RATIO = 100L;

    private final QuestionMapper questionMapper;
    private final ObjectMapper objectMapper;

    public QuestionImportServiceImpl(QuestionMapper questionMapper,
                                     ObjectMapper objectMapper) {
        this.questionMapper = questionMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionImportResultVO importFromZip(Long userId, MultipartFile file, boolean clearBeforeImport) {
        if (userId == null) {
            throw new BusinessException(401, "登录状态已失效");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要导入的 zip 文件");
        }

        String fileName = file.getOriginalFilename();
        if (!StringUtils.hasText(fileName) || !fileName.toLowerCase(Locale.ROOT).endsWith(".zip")) {
            throw new BusinessException(400, "仅支持上传 zip 文件");
        }

        File tempFile = null;
        try {
            tempFile = File.createTempFile("reviewx-question-import-", ".zip");
            file.transferTo(tempFile);
            if (clearBeforeImport) {
                QuestionImportResultVO validation = importZipFile(userId, tempFile, true, false);
                if (validation.getTotalFileCount() == 0 || validation.getTotalQuestionCount() == 0) {
                    throw new BusinessException(400, "ZIP 中没有可导入的题目 JSON，原题库未清空");
                }
                if (validation.getFailedQuestionCount() > 0) {
                    throw new BusinessException(400, "ZIP 预检失败 " + validation.getFailedQuestionCount()
                            + " 项，原题库未清空");
                }
                questionMapper.deleteByUserId(userId);
                return importZipFile(userId, tempFile, false, true);
            }
            return importZipFile(userId, tempFile, false, false);
        } catch (IOException e) {
            throw new BusinessException(500, "保存上传 zip 文件失败：" + e.getMessage(), e);
        } finally {
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    private QuestionImportResultVO importZipFile(Long userId, File zipFile, boolean dryRun, boolean failFast) {
        try {
            return importZipWithCharset(userId, zipFile, StandardCharsets.UTF_8, dryRun, failFast);
        } catch (BusinessException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            return importZipWithCharset(userId, zipFile, Charset.forName("GBK"), dryRun, failFast);
        }
    }

    private QuestionImportResultVO importZipWithCharset(Long userId, File zipFile, Charset charset,
                                                        boolean dryRun, boolean failFast) {
        QuestionImportResultVO result = new QuestionImportResultVO();
        ZipReadBudget budget = new ZipReadBudget();
        try (ZipFile zip = new ZipFile(zipFile, charset)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".json") || shouldSkip(entry.getName())) {
                    continue;
                }
                result.increaseTotalFileCount();
                try (InputStream inputStream = zip.getInputStream(entry)) {
                    importJsonFile(userId, entry.getName(), readEntry(inputStream, entry, budget), result,
                            dryRun, failFast);
                }
            }
        } catch (IOException e) {
            throw new BusinessException(500, "读取题目导入文件失败：" + charset.name() + " " + e.getMessage(), e);
        }
        return result;
    }

    /**
     * 导入单个 JSON 文件中的全部题目。
     */
    private void importJsonFile(Long userId, String fileName, byte[] content, QuestionImportResultVO result,
                                boolean dryRun, boolean failFast) {
        String questionCategory = resolveQuestionType(fileName);
        LocalDate questionJoinDate = resolveQuestionJoinDate(fileName);
        try {
            JsonNode root = objectMapper.readTree(content);
            if (!root.isArray()) {
                result.addFailure(new QuestionImportFailureVO(fileName, questionCategory, null, null, "JSON根节点不是数组"));
                return;
            }
            int index = 0;
            for (JsonNode item : root) {
                index++;
                result.increaseTotalQuestionCount();
                importQuestion(userId, fileName, questionCategory, questionJoinDate, index, item, result,
                        dryRun, failFast);
            }
        } catch (Exception e) {
            if (failFast) {
                throw e instanceof RuntimeException ? (RuntimeException) e
                        : new BusinessException(500, "题库导入失败", e);
            }
            result.addFailure(new QuestionImportFailureVO(fileName, questionCategory, null, null, "文件解析失败：" + e.getMessage()));
        }
    }

    /**
     * 导入单道题目。
     */
    private void importQuestion(Long userId, String fileName, String questionCategory, LocalDate questionJoinDate,
                                int index, JsonNode item, QuestionImportResultVO result,
                                boolean dryRun, boolean failFast) {
        String questionId = text(item, "id");
        if (!StringUtils.hasText(questionId)) {
            result.addMissingIdQuestion(new QuestionImportFailureVO(fileName, questionCategory, index, null, "题目缺少id"));
            return;
        }
        try {
            Question question = toQuestion(userId, questionId, questionCategory, questionJoinDate, item);
            if (!dryRun) {
                questionMapper.upsert(question);
            }
            result.increaseSuccessQuestionCount();
        } catch (Exception e) {
            if (failFast) {
                throw e instanceof RuntimeException ? (RuntimeException) e
                        : new BusinessException(500, "题目导入失败", e);
            }
            result.addFailure(new QuestionImportFailureVO(fileName, questionCategory, index, questionId, "题目导入失败：" + e.getMessage()));
        }
    }

    /**
     * 将 JSON 节点转换成 QUESTION 表实体。
     */
    private Question toQuestion(Long userId, String questionId, String questionCategory, LocalDate questionJoinDate, JsonNode item) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        JsonNode options = item.get("options");
        List<String> answers = answerLabels(item.get("answer"));

        Question question = new Question();
        if (questionId.length() > 255) throw new IllegalArgumentException("题目ID最多255个字符");
        if (questionCategory != null && questionCategory.length() > 64) throw new IllegalArgumentException("题目分类最多64个字符");
        question.setQuestionId(questionId);
        question.setUserId(userId);
        question.setQuestionType(answers.size() > 1 ? 2 : 1);
        question.setQuestionCategory(questionCategory);
        String questionContent = text(item, "question");
        if (!StringUtils.hasText(questionContent)) throw new IllegalArgumentException("题干不能为空");
        question.setQuestionContent(questionContent.trim());
        question.setQuestionImageBase64(firstText(item, "question_image_base64", "image_base64", "image"));
        question.setOption1(option(options, "A"));
        question.setOption2(option(options, "B"));
        question.setOption3(option(options, "C"));
        question.setOption4(option(options, "D"));
        question.setOption5(option(options, "E"));
        question.setOption6(option(options, "F"));
        question.setOption7(option(options, "G"));
        question.setOption8(option(options, "H"));
        validateAnswersMatchOptions(question, answers);
        question.setAnswerContent(objectMapper.writeValueAsString(answers));
        question.setAnswerSource(text(item, "answer_source"));
        question.setQuestionYear(text(item, "year"));
        question.setQuestionSource(text(item, "question_source"));
        question.setQuestionJoinDate(questionJoinDate);
        question.setCorrectRate(text(item, "correct_rate"));
        question.setQuestionStatus(1);
        question.setCreatedTime(now);
        question.setUpdatedTime(now);
        return question;
    }

    /**
     * 题目类型：1单选、2多选。按答案数量直接识别。
     */
    private List<String> answerLabels(JsonNode answer) {
        List<String> labels = new ArrayList<String>();
        if (answer == null || answer.isNull()) {
            throw new IllegalArgumentException("答案不能为空");
        }
        if (answer.isArray()) {
            for (JsonNode item : answer) labels.add(item.asText("").trim().toUpperCase(Locale.ROOT));
        } else {
            labels.add(answer.asText("").trim().toUpperCase(Locale.ROOT));
        }
        if (labels.isEmpty()) throw new IllegalArgumentException("答案不能为空");
        return labels;
    }

    private void validateAnswersMatchOptions(Question question, List<String> answers) {
        String[] options = new String[]{question.getOption1(), question.getOption2(), question.getOption3(), question.getOption4(),
                question.getOption5(), question.getOption6(), question.getOption7(), question.getOption8()};
        Set<String> uniqueAnswers = new HashSet<String>();
        for (String answer : answers) {
            if (answer.length() != 1 || answer.charAt(0) < 'A' || answer.charAt(0) > 'H') {
                throw new IllegalArgumentException("答案只能是A到H");
            }
            if (!uniqueAnswers.add(answer)) throw new IllegalArgumentException("答案不能重复");
            if (!StringUtils.hasText(options[answer.charAt(0) - 'A'])) {
                throw new IllegalArgumentException("答案 " + answer + " 对应的选项为空");
            }
        }
    }

    /**
     * 从 zip 路径中解析题目科目分类。
     *
     * 兼容两种常见结构：
     * 1. 分类/文件.json
     * 2. 根目录/分类/文件.json
     */
    private String resolveQuestionType(String fileName) {
        String[] parts = splitZipPath(fileName);
        if (parts.length < 2) {
            return "";
        }
        if (parts.length == 2) {
            return parts[0];
        }
        return parts[1];
    }

    /**
     * 从 JSON 文件名中解析题目加入日期。
     *
     * 文件名应为 8 位年月日，例如 20240630.json。
     */
    private LocalDate resolveQuestionJoinDate(String fileName) {
        String[] parts = splitZipPath(fileName);
        if (parts.length == 0) {
            return null;
        }
        String name = parts[parts.length - 1];
        if (!name.toLowerCase(Locale.ROOT).endsWith(".json")) {
            return null;
        }
        String baseName = name.substring(0, name.length() - 5);
        if (!baseName.matches("\\d{8}")) {
            return null;
        }
        try {
            return LocalDate.parse(baseName, JOIN_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * 判断 zip 内文件是否需要跳过。
     */
    private boolean shouldSkip(String fileName) {
        String[] parts = splitZipPath(fileName);
        return parts.length > 0
                && (LOGS_DIR_NAME.equalsIgnoreCase(parts[0])
                || "__MACOSX".equalsIgnoreCase(parts[0])
                || (parts.length >= 2 && LOGS_DIR_NAME.equalsIgnoreCase(parts[1])));
    }

    private String[] splitZipPath(String fileName) {
        if (fileName == null) {
            return new String[0];
        }
        String normalized = fileName.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }
        return normalized.split("/");
    }

    /**
     * 读取当前 zip entry 的全部字节。
     */
    private byte[] readEntry(InputStream inputStream, ZipEntry entry, ZipReadBudget budget) throws IOException {
        long declaredSize = entry.getSize();
        long compressedSize = entry.getCompressedSize();
        if (declaredSize > MAX_ENTRY_UNCOMPRESSED_BYTES) {
            throw new BusinessException(400, "ZIP 内文件解压后超过 50MB：" + entry.getName());
        }
        if (declaredSize > 0 && compressedSize > 0 && declaredSize / compressedSize > MAX_COMPRESSION_RATIO) {
            throw new BusinessException(400, "ZIP 内文件压缩比异常：" + entry.getName());
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            budget.totalBytes += len;
            if (outputStream.size() + len > MAX_ENTRY_UNCOMPRESSED_BYTES
                    || budget.totalBytes > MAX_TOTAL_UNCOMPRESSED_BYTES) {
                throw new BusinessException(400, "ZIP 解压内容超过安全限制");
            }
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
    }

    private static class ZipReadBudget {
        private long totalBytes;
    }

    private String option(JsonNode options, String key) {
        if (options == null || !options.has(key) || options.get(key).isNull()) {
            return "";
        }
        return options.get(key).asText("");
    }

    private String firstText(JsonNode node, String... fieldNames) {
        for (String fieldName : fieldNames) {
            String value = text(node, fieldName);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null || !node.has(fieldName) || node.get(fieldName).isNull()) {
            return null;
        }
        return node.get(fieldName).asText();
    }
}
