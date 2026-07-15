package com.lvwyh.reviewx.web.service.question;

import com.fasterxml.jackson.databind.JsonNode;
import com.lvwyh.reviewx.web.vo.question.QuestionJsonImportResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/** 当前用户题库的 JSON 导入导出服务。 */
public interface QuestionJsonTransferService {

    interface ProgressListener {
        void update(int processedCount, int totalCount);
    }

    /** 按范围导出当前用户的全部题库数据。 */
    JsonNode exportJson(Long userId, String scope);

    JsonNode exportJson(Long userId, String scope, ProgressListener listener);

    /** 按范围导入 JSON，题目以 questionId 幂等写入。 */
    QuestionJsonImportResultVO importJson(Long userId, String scope, MultipartFile file);

    QuestionJsonImportResultVO importJson(Long userId, String scope, InputStream inputStream,
                                          String fileName, ProgressListener listener);
}
