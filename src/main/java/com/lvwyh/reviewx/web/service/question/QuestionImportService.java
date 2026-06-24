package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.vo.question.QuestionImportResultVO;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionImportService {

    /**
     * 从上传的 zip 文件导入题目。
     */
    QuestionImportResultVO importFromZip(Long userId, MultipartFile file, boolean clearBeforeImport);
}
