package com.yk.Motivation.domain.qna.entity;

import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.standard.util.DateUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity  // JPA Entity로 선언
@Setter  // Lombok: Setter 자동 생성
@Getter  // Lombok: Getter 자동 생성
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder  // Lombok: Builder 패턴 구현
@ToString(callSuper = true)  // Lombok: toString 메서드 오버라이드
public class Question extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Lecture lecture;  // 강의 ID

    @ManyToOne(fetch = FetchType.LAZY)
    private Lesson lesson;  // 레슨 ID - 인덱스 걸어서 검색 속도 향상 시키기, 젠파일에 인덱스 거는법 있음

    @Column(nullable = false, length = 255)
    private String subject;  // 질문 제목

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String bodyHtml;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;

    public String getFormattedDate() {
        // BaseEntity의 createDate를 사용
        return DateUtils.timeForToday(this.getCreateDate());
    }

    // 조회 수
    private int viewCount = 0;
    // 추천 수
    private int voteCount = 0; // 초기 값 0
}
