package com.lvwyh.reviewx.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lvwyh.reviewx.web.dto.question.QuestionFilterOptionsDTO;
import com.lvwyh.reviewx.web.mapper.question.QuestionMapper;
import com.lvwyh.reviewx.web.service.question.impl.QuestionServiceImpl;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.*;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/** 执行真实 Mapper SQL，以独立内存数据验证交叉筛选及取题总数一致性。 */
public class PracticeFilterOptionsTest {
    public static String dateFormat(java.sql.Date date, String pattern) {
        return date == null ? null : date.toLocalDate().toString();
    }

    @Test
    void facetsRespectOtherSelectionsAnalysisOwnershipAndEnabledStatus() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:mem:facets;MODE=MySQL;DB_CLOSE_DELAY=-1");
        List<String[]> rows = Arrays.asList(
                new String[]{"言语理解", "2026", "国考", "2026-10-01", "analyzed"},
                new String[]{"判断推理", "2026", "省考", "2026-10-02", "analyzed"},
                new String[]{"言语理解", "2025", "省考", "2026-10-02", "unanalyzed"},
                new String[]{"判断推理", "2026", "国考", "2026-10-03", "unanalyzed"},
                new String[]{null, null, null, null, "unanalyzed"});
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.execute("CREATE ALIAS DATE_FORMAT FOR 'com.lvwyh.reviewx.web.PracticeFilterOptionsTest.dateFormat'");
            s.execute("CREATE TABLE QUESTION (USER_ID BIGINT, QUESTION_ID VARCHAR(255), QUESTION_CATEGORY VARCHAR(64), QUESTION_YEAR VARCHAR(255), QUESTION_SOURCE VARCHAR(255), QUESTION_JOIN_DATE DATE, QUESTION_STATUS INT, PRIMARY KEY(USER_ID, QUESTION_ID))");
            s.execute("CREATE TABLE QUESTION_ANALYSIS (USER_ID BIGINT, QUESTION_ID VARCHAR(255), ANALYSIS_CONTENT VARCHAR(255), ANALYSIS_IMAGE_BASE64 VARCHAR(255), PRIMARY KEY(USER_ID, QUESTION_ID))");
            for (int i = 0; i < rows.size(); i++) {
                String[] row = rows.get(i);
                try (java.sql.PreparedStatement p = c.prepareStatement("INSERT INTO QUESTION VALUES (1, ?, ?, ?, ?, ?, 1)")) {
                    p.setString(1, "q" + i);
                    for (int j = 0; j < 4; j++) p.setString(j + 2, row[j]);
                    p.executeUpdate();
                }
            }
            s.execute("INSERT INTO QUESTION_ANALYSIS VALUES (1,'q0','文本',NULL), (1,'q1',NULL,'图片'), (1,'q3','',''), (2,'q2','他人解析',NULL), (1,'orphan','孤立解析',NULL)");
            s.execute("INSERT INTO QUESTION VALUES (2,'foreign','他人分类','2024','他人来源','2024-01-01',1), (1,'disabled','禁用分类','2023','禁用来源','2023-01-01',0)");
        }
        Configuration config = new Configuration(new Environment("test", new JdbcTransactionFactory(), ds));
        try (InputStream input = getClass().getResourceAsStream("/mapper/question/QuestionMapper.xml")) {
            new XMLMapperBuilder(input, config, "QuestionMapper.xml", config.getSqlFragments()).parse();
        }
        try (SqlSession session = new SqlSessionFactoryBuilder().build(config).openSession()) {
            QuestionMapper mapper = session.getMapper(QuestionMapper.class);
            QuestionServiceImpl service = new QuestionServiceImpl(mapper, null, new ObjectMapper());
            for (String scope : Arrays.asList("all", "analyzed", "unanalyzed"))
                for (String category : Arrays.asList("", "言语理解", "判断推理", "不存在"))
                    for (String year : Arrays.asList("", "2026", "2025"))
                        for (String source : Arrays.asList("", "国考", "省考"))
                            for (String date : Arrays.asList("", "2026-10-01", "2026-10-02", "2026-10-03")) {
                                String[] selected = {category, year, source, date};
                                QuestionFilterOptionsDTO result = service.practiceFilterOptions(1L, category, year, source, date, scope);
                                List<List<String>> actual = Arrays.asList(result.getQuestionTypes(), result.getQuestionYears(), result.getQuestionSources(), result.getQuestionJoinDates());
                                for (int dimension = 0; dimension < 4; dimension++) {
                                    final int facet = dimension;
                                    List<String> expected = rows.stream().filter(row -> matches(row, selected, scope, facet))
                                            .map(row -> row[facet]).filter(Objects::nonNull).filter(v -> !v.isEmpty()).distinct()
                                            .sorted(facet == 1 || facet == 3 ? Comparator.reverseOrder() : Comparator.naturalOrder()).collect(Collectors.toList());
                                    assertEquals(expected, actual.get(facet), scope + Arrays.toString(selected) + " facet=" + facet);
                                }
                                long expectedCount = rows.stream().filter(row -> matches(row, selected, scope, -1)).count();
                                assertEquals(expectedCount, mapper.countOrder(1L, category, year, source, date, scope));
                            }
            assertTrue(service.practiceFilterOptions(99L, null, null, null, null, "all").getQuestionTypes().isEmpty());
        }
    }

    private boolean matches(String[] row, String[] selected, String scope, int ignored) {
        if (!"all".equals(scope) && !scope.equals(row[4])) return false;
        for (int i = 0; i < 4; i++) {
            if (i != ignored && !selected[i].isEmpty() && !selected[i].equals(row[i])) return false;
        }
        return true;
    }
}
