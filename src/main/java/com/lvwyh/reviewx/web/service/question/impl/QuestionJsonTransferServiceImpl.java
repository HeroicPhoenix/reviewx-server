package com.lvwyh.reviewx.web.service.question.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lvwyh.reviewx.web.common.exception.BusinessException;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.entity.question.QuestionAnalysis;
import com.lvwyh.reviewx.web.mapper.question.QuestionAnalysisMapper;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.QuestionJsonTransferService;
import com.lvwyh.reviewx.web.vo.question.QuestionJsonImportResultVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 当前用户题库的 JSON 导入导出实现。 */
@Service
public class QuestionJsonTransferServiceImpl implements QuestionJsonTransferService {

    private static final String SCOPE_QUESTION = "question";
    private static final String SCOPE_ANALYSIS = "analysis";
    private static final String SCOPE_ALL = "question_analysis";
    private static final int EXPORT_PAGE_SIZE = 100;

    private final QuestionMapper questionMapper;
    private final QuestionAnalysisMapper questionAnalysisMapper;
    private final ObjectMapper objectMapper;

    public QuestionJsonTransferServiceImpl(QuestionMapper questionMapper,
                                           QuestionAnalysisMapper questionAnalysisMapper,
                                           ObjectMapper objectMapper) {
        this.questionMapper = questionMapper;
        this.questionAnalysisMapper = questionAnalysisMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportJsonTo(Long userId, String scope, OutputStream outputStream, ProgressListener listener) {
        String validScope = validScope(scope);
        long totalValue = questionMapper.countForExport(userId);
        int total = totalValue > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) totalValue;
        int processed = 0;
        int offset = 0;
        notifyProgress(listener, 0, total);
        try (JsonGenerator generator = objectMapper.getFactory().createGenerator(outputStream)) {
            generator.writeStartObject();
            generator.writeNumberField("schemaVersion", 1);
            generator.writeStringField("scope", validScope);
            generator.writeStringField("exportedAt", LocalDateTime.now().toString());
            generator.writeArrayFieldStart("items");
            while (true) {
                java.util.List<Question> questions = questionMapper.selectForExportPage(userId, offset, EXPORT_PAGE_SIZE);
                if (questions == null || questions.isEmpty()) break;
                for (Question question : questions) {
                    ObjectNode item = objectMapper.createObjectNode();
                    item.put("questionId", question.getQuestionId());
                    if (!SCOPE_ANALYSIS.equals(validScope)) {
                        writeQuestion(item, question);
                    }
                    if (!SCOPE_QUESTION.equals(validScope)) {
                        putNullable(item, "analysisContent", question.getAnalysisContent());
                        putNullable(item, "analysisImageBase64", question.getAnalysisImageBase64());
                    }
                    generator.writeTree(item);
                    processed++;
                    notifyProgress(listener, processed, total);
                }
                offset += questions.size();
                if (questions.size() < EXPORT_PAGE_SIZE) break;
            }
            generator.writeEndArray();
            generator.writeEndObject();
            generator.flush();
        } catch (IOException e) {
            throw new BusinessException(500, "题库导出失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionJsonImportResultVO importJson(Long userId, String scope, MultipartFile file) {
        validateFile(file);
        try {
            return importJson(userId, scope, file.getInputStream(), file.getOriginalFilename(), null);
        } catch (IOException e) {
            throw new BusinessException(400, "JSON 文件读取失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QuestionJsonImportResultVO importJson(Long userId, String scope, InputStream inputStream,
                                                 String fileName, ProgressListener listener) {
        String validScope = validScope(scope);
        validateFileName(fileName);
        JsonNode root;
        try {
            root = objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new BusinessException(400, "JSON 文件解析失败：" + e.getMessage(), e);
        }
        JsonNode items = root == null ? null : root.get("items");
        if (items == null || !items.isArray()) {
            throw new BusinessException(400, "JSON 根节点必须包含 items 数组");
        }

        QuestionJsonImportResultVO result = new QuestionJsonImportResultVO();
        int total = items.size();
        int index = 0;
        notifyProgress(listener, 0, total);
        for (JsonNode item : items) {
            index++;
            result.increaseTotalCount();
            String questionId = text(item, "questionId");
            try {
                if (!StringUtils.hasText(questionId)) {
                    throw new IllegalArgumentException("缺少 questionId");
                }
                if (!SCOPE_ANALYSIS.equals(validScope)) {
                    questionMapper.upsert(toQuestion(userId, questionId.trim(), item));
                } else if (questionMapper.selectById(userId, questionId.trim()) == null) {
                    throw new IllegalArgumentException("当前题库不存在该题目，无法导入解析");
                }
                if (!SCOPE_QUESTION.equals(validScope)) {
                    questionAnalysisMapper.upsert(toAnalysis(userId, questionId.trim(), item));
                }
                result.increaseSuccessCount();
            } catch (Exception e) {
                String idText = StringUtils.hasText(questionId) ? "，questionId=" + questionId : "";
                result.addFailure("第 " + index + " 项" + idText + "：" + e.getMessage());
            }
            notifyProgress(listener, index, total);
        }
        return result;
    }

    private void writeQuestion(ObjectNode item, Question question) {
        if (question.getQuestionType() != null) item.put("questionType", question.getQuestionType());
        putNullable(item, "questionCategory", question.getQuestionCategory());
        putNullable(item, "questionContent", question.getQuestionContent());
        putNullable(item, "questionImageBase64", question.getQuestionImageBase64());
        putNullable(item, "option1", question.getOption1());
        putNullable(item, "option2", question.getOption2());
        putNullable(item, "option3", question.getOption3());
        putNullable(item, "option4", question.getOption4());
        putNullable(item, "option5", question.getOption5());
        putNullable(item, "option6", question.getOption6());
        putNullable(item, "option7", question.getOption7());
        putNullable(item, "option8", question.getOption8());
        try {
            JsonNode answers = objectMapper.readTree(question.getAnswerContent());
            item.set("answerContent", answers != null && answers.isArray() ? answers : objectMapper.createArrayNode());
        } catch (Exception e) {
            item.set("answerContent", objectMapper.createArrayNode());
        }
        putNullable(item, "answerSource", question.getAnswerSource());
        putNullable(item, "questionYear", question.getQuestionYear());
        putNullable(item, "questionSource", question.getQuestionSource());
        if (question.getQuestionJoinDate() != null) item.put("questionJoinDate", question.getQuestionJoinDate().toString());
        putNullable(item, "correctRate", question.getCorrectRate());
    }

    private Question toQuestion(Long userId, String questionId, JsonNode item) throws IOException {
        String questionContent = text(item, "questionContent");
        JsonNode answerContent = item.get("answerContent");
        if (!StringUtils.hasText(questionContent)) {
            throw new IllegalArgumentException("questionContent 不能为空");
        }
        if (answerContent == null || !answerContent.isArray() || answerContent.size() == 0) {
            throw new IllegalArgumentException("answerContent 必须是非空数组");
        }
        List<String> answers = answerLabels(answerContent);
        LocalDateTime now = LocalDateTime.now();
        Question question = new Question();
        question.setQuestionId(questionId);
        question.setUserId(userId);
        question.setQuestionType(answers.size() > 1 ? 2 : 1);
        question.setQuestionCategory(trimToNull(text(item, "questionCategory")));
        question.setQuestionContent(questionContent.trim());
        question.setQuestionImageBase64(trimToNull(text(item, "questionImageBase64")));
        question.setOption1(trimToEmpty(text(item, "option1")));
        question.setOption2(trimToEmpty(text(item, "option2")));
        question.setOption3(trimToEmpty(text(item, "option3")));
        question.setOption4(trimToEmpty(text(item, "option4")));
        question.setOption5(trimToEmpty(text(item, "option5")));
        question.setOption6(trimToEmpty(text(item, "option6")));
        question.setOption7(trimToEmpty(text(item, "option7")));
        question.setOption8(trimToEmpty(text(item, "option8")));
        validateAnswersMatchOptions(question, answers);
        question.setAnswerContent(objectMapper.writeValueAsString(answers));
        question.setAnswerSource(trimToNull(text(item, "answerSource")));
        question.setQuestionYear(trimToNull(text(item, "questionYear")));
        question.setQuestionSource(trimToNull(text(item, "questionSource")));
        question.setQuestionJoinDate(parseDate(text(item, "questionJoinDate")));
        question.setCorrectRate(trimToNull(text(item, "correctRate")));
        question.setQuestionStatus(1);
        question.setCreatedTime(now);
        question.setUpdatedTime(now);
        return question;
    }

    private QuestionAnalysis toAnalysis(Long userId, String questionId, JsonNode item) {
        LocalDateTime now = LocalDateTime.now();
        QuestionAnalysis analysis = new QuestionAnalysis();
        analysis.setUserId(userId);
        analysis.setQuestionId(questionId);
        analysis.setAnalysisContent(trimToNull(text(item, "analysisContent")));
        analysis.setAnalysisImageBase64(trimToNull(text(item, "analysisImageBase64")));
        analysis.setCreatedTime(now);
        analysis.setUpdatedTime(now);
        return analysis;
    }

    private String validScope(String scope) {
        String value = scope == null ? "" : scope.trim().toLowerCase(Locale.ROOT);
        if (SCOPE_QUESTION.equals(value) || SCOPE_ANALYSIS.equals(value) || SCOPE_ALL.equals(value)) {
            return value;
        }
        throw new BusinessException(400, "scope 仅支持 question、analysis、question_analysis");
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要导入的 JSON 文件");
        }
        validateFileName(file.getOriginalFilename());
    }

    private void validateFileName(String name) {
        if (!StringUtils.hasText(name) || !name.toLowerCase(Locale.ROOT).endsWith(".json")) {
            throw new BusinessException(400, "仅支持上传 .json 文件");
        }
    }

    private void notifyProgress(ProgressListener listener, int processed, int total) {
        if (listener != null) listener.update(processed, total);
    }

    private LocalDate parseDate(String value) {
        if (!StringUtils.hasText(value)) return null;
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("questionJoinDate 必须为 YYYY-MM-DD");
        }
    }

    private void putNullable(ObjectNode node, String field, String value) {
        if (value == null) node.putNull(field); else node.put(field, value);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node == null ? null : node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private List<String> answerLabels(JsonNode answerContent) {
        List<String> answers = new ArrayList<String>();
        for (JsonNode answer : answerContent) {
            answers.add(answer.asText("").trim().toUpperCase(Locale.ROOT));
        }
        return answers;
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
}
