package com.lvwyh.reviewx.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.entity.question.Question;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.impl.QuestionImportServiceImpl;
import com.lvwyh.reviewx.web.vo.question.QuestionImportResultVO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/** 中文 ZIP 文件名编码兼容回归。 */
class QuestionImportServiceImplZipCharsetTest {

    @Test
    void gbkEncodedChineseEntryNameMustBeImported() throws Exception {
        QuestionMapper mapper = mock(QuestionMapper.class);
        QuestionImportServiceImpl service = new QuestionImportServiceImpl(mapper, new ObjectMapper());
        String json = "[{\"id\":\"q-gbk-1\",\"question\":\"测试题\","
                + "\"options\":{\"A\":\"正确\",\"B\":\"错误\"},\"answer\":[\"A\"]}]";

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(bytes, Charset.forName("GBK"))) {
            zip.putNextEntry(new ZipEntry("题库/常识判断/20260807.json"));
            zip.write(json.getBytes(StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        MockMultipartFile file = new MockMultipartFile(
                "file", "中文题库.zip", "application/zip", bytes.toByteArray());

        QuestionImportResultVO result = service.importFromZip(7L, file, false);

        assertEquals(1, result.getSuccessQuestionCount());
        ArgumentCaptor<Question> question = ArgumentCaptor.forClass(Question.class);
        verify(mapper).upsert(question.capture());
        assertEquals("常识判断", question.getValue().getQuestionCategory());
        assertEquals(LocalDate.of(2026, 8, 7), question.getValue().getQuestionJoinDate());
    }
}
