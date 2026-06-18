package com.lvwyh.reviewx.web.mapper.question;

import com.lvwyh.reviewx.web.entity.question.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper {

    Question selectById(@Param("questionId") String questionId);

    List<Question> search(@Param("keyword") String keyword,
                          @Param("questionYear") String questionYear,
                          @Param("questionSource") String questionSource,
                          @Param("offset") int offset,
                          @Param("pageSize") int pageSize);

    long countSearch(@Param("keyword") String keyword,
                     @Param("questionYear") String questionYear,
                     @Param("questionSource") String questionSource);

    List<Question> selectRandom(@Param("questionYear") String questionYear,
                                @Param("questionSource") String questionSource,
                                @Param("size") int size);

    List<Question> selectOrder(@Param("questionYear") String questionYear,
                               @Param("questionSource") String questionSource,
                               @Param("offset") int offset,
                               @Param("pageSize") int pageSize);

    List<Question> selectWrong(@Param("userId") Long userId,
                               @Param("questionYear") String questionYear,
                               @Param("questionSource") String questionSource,
                               @Param("size") int size);
}
