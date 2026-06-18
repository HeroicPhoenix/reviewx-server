package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.AnswerRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AnswerRecordMapper {

    int insert(AnswerRecord record);

    List<AnswerRecord> selectPage(@Param("userId") Long userId,
                                  @Param("questionId") String questionId,
                                  @Param("isCorrect") Integer isCorrect,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  @Param("offset") int offset,
                                  @Param("pageSize") int pageSize);

    long countPage(@Param("userId") Long userId,
                   @Param("questionId") String questionId,
                   @Param("isCorrect") Integer isCorrect,
                   @Param("startTime") LocalDateTime startTime,
                   @Param("endTime") LocalDateTime endTime);

    Long countTotal(@Param("userId") Long userId);

    Long countCorrect(@Param("userId") Long userId);

    Long avgDurationMs(@Param("userId") Long userId);
}
