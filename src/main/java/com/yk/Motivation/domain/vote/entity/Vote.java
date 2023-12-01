package com.yk.Motivation.domain.vote.entity;

import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.qna.entity.Question;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PROTECTED;

@Entity  // JPA Entity로 선언
@Setter  // Lombok: Setter 자동 생성
@Getter  // Lombok: Getter 자동 생성
@AllArgsConstructor(access = PROTECTED)  // 모든 필드 값을 인자로 받는 생성자 생성
@NoArgsConstructor(access = PROTECTED)
@SuperBuilder  // Lombok: Builder 패턴 구현
@ToString(callSuper = true)  // Lombok: toString 메서드 오버라이드
public class Vote extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    private String nickName; // member는 nickname만 기록

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;
}
