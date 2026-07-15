package com.lvwyh.reviewx.web.service.question;

import com.lvwyh.reviewx.web.vo.question.QuestionTransferTaskVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/** 题库 JSON 导入导出后台任务服务。 */
public interface QuestionTransferTaskService {

    QuestionTransferTaskVO startExport(Long userId, String scope);

    QuestionTransferTaskVO startImport(Long userId, String scope, MultipartFile file);

    QuestionTransferTaskVO status(Long userId, String taskId);

    Resource exportFile(Long userId, String taskId);
}
