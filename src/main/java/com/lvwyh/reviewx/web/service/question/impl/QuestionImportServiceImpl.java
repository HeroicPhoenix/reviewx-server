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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
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

    private final QuestionMapper questionMapper;
    private final ObjectMapper objectMapper;

    public QuestionImportServiceImpl(QuestionMapper questionMapper, ObjectMapper objectMapper) {
        this.questionMapper = questionMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public QuestionImportResultVO importFromZip(Long userId, MultipartFile file) {
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
            return importZipFile(userId, tempFile);
        } catch (IOException e) {
            throw new BusinessException(500, "保存上传 zip 文件失败：" + e.getMessage(), e);
        } finally {
            if (tempFile != null && tempFile.exists() && !tempFile.delete()) {
                tempFile.deleteOnExit();
            }
        }
    }

    private QuestionImportResultVO importZipFile(Long userId, File zipFile) {
        try {
            return importZipWithCharset(userId, zipFile, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return importZipWithCharset(userId, zipFile, Charset.forName("GBK"));
        }
    }

    private QuestionImportResultVO importZipWithCharset(Long userId, File zipFile, Charset charset) {
        QuestionImportResultVO result = new QuestionImportResultVO();
        try (ZipFile zip = new ZipFile(zipFile, charset)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory() || !entry.getName().endsWith(".json") || shouldSkip(entry.getName())) {
                    continue;
                }
                result.increaseTotalFileCount();
                try (InputStream inputStream = zip.getInputStream(entry)) {
                    importJsonFile(userId, entry.getName(), readEntry(inputStream), result);
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
    private void importJsonFile(Long userId, String fileName, byte[] content, QuestionImportResultVO result) {
        String questionType = resolveQuestionType(fileName);
        try {
            JsonNode root = objectMapper.readTree(content);
            if (!root.isArray()) {
                result.addFailure(new QuestionImportFailureVO(fileName, questionType, null, null, "JSON根节点不是数组"));
                return;
            }
            int index = 0;
            for (JsonNode item : root) {
                index++;
                result.increaseTotalQuestionCount();
                importQuestion(userId, fileName, questionType, index, item, result);
            }
        } catch (Exception e) {
            result.addFailure(new QuestionImportFailureVO(fileName, questionType, null, null, "文件解析失败：" + e.getMessage()));
        }
    }

    /**
     * 导入单道题目。
     */
    private void importQuestion(Long userId, String fileName, String questionType, int index, JsonNode item, QuestionImportResultVO result) {
        String questionId = text(item, "id");
        if (!StringUtils.hasText(questionId)) {
            result.addMissingIdQuestion(new QuestionImportFailureVO(fileName, questionType, index, null, "题目缺少id"));
            return;
        }
        try {
            Question question = toQuestion(userId, questionId, questionType, item);
            questionMapper.upsert(question);
            result.increaseSuccessQuestionCount();
        } catch (Exception e) {
            result.addFailure(new QuestionImportFailureVO(fileName, questionType, index, questionId, "题目导入失败：" + e.getMessage()));
        }
    }

    /**
     * 将 JSON 节点转换成 QUESTION 表实体。
     */
    private Question toQuestion(Long userId, String questionId, String questionType, JsonNode item) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        JsonNode options = item.get("options");

        Question question = new Question();
        question.setQuestionId(questionId);
        question.setUserId(userId);
        question.setQuestionType(questionType);
        question.setQuestionContent(text(item, "question"));
        question.setQuestionImageBase64(firstText(item, "question_image_base64", "image_base64", "image"));
        question.setOption1(option(options, "A"));
        question.setOption2(option(options, "B"));
        question.setOption3(option(options, "C"));
        question.setOption4(option(options, "D"));
        question.setOption5(option(options, "E"));
        question.setOption6(option(options, "F"));
        question.setOption7(option(options, "G"));
        question.setOption8(option(options, "H"));
        question.setAnswerContent(item.has("answer") ? objectMapper.writeValueAsString(item.get("answer")) : "[]");
        question.setAnswerSource(text(item, "answer_source"));
        question.setQuestionYear(text(item, "year"));
        question.setQuestionSource(text(item, "question_source"));
        question.setCorrectRate(text(item, "correct_rate"));
        question.setQuestionStatus(1);
        question.setCreatedTime(now);
        question.setUpdatedTime(now);
        return question;
    }

    /**
     * 从 zip 路径中解析题目类型。
     */
    private String resolveQuestionType(String fileName) {
        String[] parts = fileName.split("/");
        return parts.length >= 3 ? parts[1] : "";
    }

    /**
     * 判断 zip 内文件是否需要跳过。
     */
    private boolean shouldSkip(String fileName) {
        String[] parts = fileName.split("/");
        return parts.length >= 2 && LOGS_DIR_NAME.equalsIgnoreCase(parts[1]);
    }

    /**
     * 读取当前 zip entry 的全部字节。
     */
    private byte[] readEntry(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        return outputStream.toByteArray();
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
