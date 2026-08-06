package com.lvwyh.reviewx.web;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 构建期守卫：永久记录不得因后续开发被加入删除 SQL。 */
class PersistentRecordSqlInvariantTest {

    @Test
    void answerRecordMapperMustRemainAppendOnly() throws IOException {
        String mapper = classpathText("mapper/question/AnswerRecordMapper.xml").toUpperCase(Locale.ROOT);
        assertFalse(mapper.contains("<DELETE"), "作答记录 Mapper 禁止提供删除语句");
        assertFalse(mapper.contains("<UPDATE"), "作答记录 Mapper 禁止提供更新语句");
        assertFalse(mapper.contains("DELETE FROM ANSWER_RECORD"), "作答记录禁止 DELETE");
        assertFalse(mapper.contains("TRUNCATE TABLE ANSWER_RECORD"), "作答记录禁止 TRUNCATE");
        assertFalse(mapper.contains("REPLACE INTO ANSWER_RECORD"), "作答记录禁止 REPLACE");
    }

    @Test
    void analysisMapperMustOnlyUpsertTheCompositePrimaryKey() throws IOException {
        String mapper = classpathText("mapper/question/QuestionAnalysisMapper.xml").toUpperCase(Locale.ROOT);
        assertFalse(mapper.contains("<DELETE"), "解析 Mapper 禁止提供删除语句");
        assertFalse(mapper.contains("DELETE FROM QUESTION_ANALYSIS"), "解析记录禁止 DELETE");
        assertFalse(mapper.contains("TRUNCATE TABLE QUESTION_ANALYSIS"), "解析记录禁止 TRUNCATE");
        assertTrue(mapper.contains("ON DUPLICATE KEY UPDATE"), "解析只能通过复合主键幂等覆盖");
    }

    @Test
    void initializationDdlMustRemainNonDestructive() throws IOException {
        String ddl = new String(Files.readAllBytes(Paths.get("docs", "ddl.sql")), StandardCharsets.UTF_8)
                .toUpperCase(Locale.ROOT);
        assertFalse(ddl.contains("DROP TABLE"), "初始化脚本禁止 DROP TABLE");
        assertFalse(ddl.contains("TRUNCATE TABLE"), "初始化脚本禁止 TRUNCATE TABLE");
        assertFalse(ddl.contains("ON DELETE CASCADE"), "永久记录禁止外键级联删除");
        int analysisStart = ddl.indexOf("CREATE TABLE IF NOT EXISTS `QUESTION_ANALYSIS`");
        int analysisEnd = ddl.indexOf("ENGINE=INNODB", analysisStart);
        assertTrue(analysisStart >= 0 && analysisEnd > analysisStart, "初始化脚本必须包含解析表");
        String analysisDdl = ddl.substring(analysisStart, analysisEnd);
        assertTrue(analysisDdl.contains("PRIMARY KEY (`USER_ID`, `QUESTION_ID`)"),
                "解析必须使用 (USER_ID, QUESTION_ID) 复合主键");
    }

    @Test
    void noApplicationSourceMayDeletePermanentRecords() throws IOException {
        String source = sourceTreeText(Paths.get("src", "main")).toUpperCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
        assertFalse(source.contains("DELETE FROM ANSWER_RECORD"), "任何应用源码都禁止删除作答记录");
        assertFalse(source.contains("DELETE FROM QUESTION_ANALYSIS"), "任何应用源码都禁止删除解析记录");
        assertFalse(source.contains("TRUNCATE TABLE ANSWER_RECORD"), "任何应用源码都禁止清空作答记录");
        assertFalse(source.contains("TRUNCATE TABLE QUESTION_ANALYSIS"), "任何应用源码都禁止清空解析记录");
    }

    private String classpathText(String path) throws IOException {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        assertNotNull(input, "找不到测试资源：" + path);
        try (InputStream stream = input; ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int length;
            while ((length = stream.read(buffer)) >= 0) {
                output.write(buffer, 0, length);
            }
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private String sourceTreeText(java.nio.file.Path root) throws IOException {
        StringBuilder result = new StringBuilder();
        try (Stream<java.nio.file.Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile).forEach(path -> {
                try {
                    result.append(new String(Files.readAllBytes(path), StandardCharsets.UTF_8)).append('\n');
                } catch (IOException e) {
                    throw new IllegalStateException("读取源码失败：" + path, e);
                }
            });
        }
        return result.toString();
    }
}
