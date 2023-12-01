package com.yk.Motivation.domain.qna.repository;

import com.yk.Motivation.domain.qna.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    /*@Query("SELECT q FROM Question q WHERE q.lecture.id IS NOT NULL AND q.lecture.id = :lectureId " +
            "ORDER BY CASE " +
            "WHEN q.lesson.id = :lessonId AND q.member.id = :memberId THEN 1 " +
            "WHEN q.lesson.id = :lessonId THEN 2 " +
            "WHEN q.lecture.id = :lectureId AND q.member.id = :memberId THEN 3 " +
            "ELSE 4 END, q.id")
    Page<Question> findWithPriority(@Param("lectureId") long lectureId, @Param("lessonId") long lessonId, @Param("memberId") long memberId, Pageable pageable);*/
    @Query("SELECT q FROM Question q WHERE q.lecture.id IS NOT NULL AND q.lecture.id = :lectureId " +
            "ORDER BY CASE " +
            "WHEN q.lesson.id = :lessonId AND q.member.id = :memberId THEN 1 " +
            "WHEN q.lesson.id = :lessonId THEN 2 " +
            "WHEN q.lecture.id = :lectureId AND q.member.id = :memberId THEN 3 " +
            "ELSE 4 END, q.createDate DESC, q.id")
    Page<Question> findWithPriority(@Param("lectureId") long lectureId, @Param("lessonId") long lessonId, @Param("memberId") long memberId, Pageable pageable);
}
