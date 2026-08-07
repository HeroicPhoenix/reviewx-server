package com.lvwyh.reviewx.web;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 列表查询禁止重新引入 Base64 大字段。 */
class QuestionMapperProjectionInvariantTest {

    @Test
    void listAndPracticeProjectionsMustExcludeBase64Columns() throws IOException {
        String mapper = classpathText("mapper/question/QuestionMapper.xml");
        String listProjection = section(mapper, "<sql id=\"Q_List_Column_List\">", "</sql>");
        String practiceProjection = section(mapper, "<sql id=\"Q_Practice_Column_List\">", "</sql>");

        assertFalse(listProjection.contains("BASE64"));
        assertFalse(practiceProjection.contains("BASE64"));
        assertFalse(practiceProjection.contains("ANSWER_CONTENT"));
        assertTrue(section(mapper, "<select id=\"search\"", "</select>")
                .contains("refid=\"Q_List_Column_List\""));
        assertTrue(section(mapper, "<select id=\"selectRandom\"", "</select>")
                .contains("refid=\"Q_Practice_Column_List\""));
        assertTrue(section(mapper, "<select id=\"selectOrder\"", "</select>")
                .contains("refid=\"Q_Practice_Column_List\""));
        assertTrue(section(mapper, "<select id=\"selectWrong\"", "</select>")
                .contains("refid=\"Q_Practice_Column_List\""));
    }

    private String section(String text, String startMarker, String endMarker) {
        int start = text.indexOf(startMarker);
        int end = text.indexOf(endMarker, start);
        return text.substring(start, end);
    }

    private String classpathText(String path) throws IOException {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
            if (input == null) throw new IOException("Missing classpath resource: " + path);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int length;
            while ((length = input.read(buffer)) != -1) output.write(buffer, 0, length);
            return new String(output.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
