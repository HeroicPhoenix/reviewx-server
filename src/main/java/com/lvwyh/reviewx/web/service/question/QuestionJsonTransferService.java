package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.vo.question.QuestionJsonImportResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.OutputStream;

/** 当前用户题库的 JSON 导入导出服务。 */
public interface QuestionJsonTransferService {

    interface ProgressListener {
        void update(int processedCount, int totalCount);
    }

    /** 分页读取并流式导出当前用户题库，避免整库数据同时驻留内存。 */
    void exportJsonTo(Long userId, String scope, OutputStream outputStream, ProgressListener listener);

    /** 按范围导入 JSON，题目以 questionId 幂等写入。 */
    QuestionJsonImportResultVO importJson(Long userId, String scope, MultipartFile file);

    QuestionJsonImportResultVO importJson(Long userId, String scope, InputStream inputStream,
                                          String fileName, ProgressListener listener);
}
