package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.vo.question.QuestionImportResultVO;

public interface QuestionImportService {

    /**
     * 从 docs/识别结果id输出.zip 导入题目。
     */
    QuestionImportResultVO importFromDocsZip();
}
