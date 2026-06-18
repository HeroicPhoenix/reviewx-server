package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.AnswerRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 答题记录 Mapper。
 *
 * 负责 ANSWER_RECORD 表写入、分页查询和用户维度统计。
 */
@Mapper
public interface AnswerRecordMapper {

    /** 新增一次提交答案记录，插入后回填 answerRecordId。 */
    int insert(AnswerRecord record);

    /** 分页查询当前用户的答题记录。 */
    List<AnswerRecord> selectPage(@Param("userId") Long userId,
                                  @Param("questionId") String questionId,
                                  @Param("isCorrect") Integer isCorrect,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    /** 统计当前用户答题记录分页总数。 */
    long countPage(@Param("userId") Long userId,
                   @Param("questionId") String questionId,
                   @Param("isCorrect") Integer isCorrect,
                   @Param("startTime") LocalDateTime startTime,
                   @Param("endTime") LocalDateTime endTime);

    /** 统计当前用户总作答次数。 */
    Long countTotal(@Param("userId") Long userId);

    /** 统计当前用户答对次数。 */
    Long countCorrect(@Param("userId") Long userId);

    /** 统计当前用户平均作答耗时，单位毫秒。 */
    Long avgDurationMs(@Param("userId") Long userId);
}
