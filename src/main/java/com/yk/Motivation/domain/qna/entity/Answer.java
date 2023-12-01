package com.yk.Motivation.domain.qna.entity;

import com.yk.Motivation.base.jpa.baseEntity.BaseEntity;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.standard.util.DateUtils;
import jakarta.persistence.*;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity  // JPA Entity로 선언
@Setter  // Lombok: Setter 자동 생성
@Getter  // Lombok: Getter 자동 생성
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder  // Lombok: Builder 패턴 구현
@ToString(callSuper = true)  // Lombok: toString 메서드 오버라이드
public class Answer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    private Question question;  // 해당 답변과 관련된 질문

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 답변 내용
    @Column(columnDefinition = "TEXT")
    private String contentHtml;

    @ManyToOne
    private Member member;

    public String getFormattedDate() {
        // BaseEntity의 createDate를 사용
        return DateUtils.timeForToday(this.getCreateDate());
    }
}
