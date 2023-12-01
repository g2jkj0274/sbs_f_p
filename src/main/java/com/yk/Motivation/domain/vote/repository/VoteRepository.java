package com.yk.Motivation.domain.vote.repository;

import com.yk.Motivation.domain.vote.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByNickNameAndArticleId(String nickName, Long articleId);
    Optional<Vote> findByNickNameAndArticleId(String nickName, Long articleId);

    boolean existsByNickNameAndQuestionId(String nickName, Long questionId);
    Optional<Vote> findByNickNameAndQuestionId(String nickName, Long questionId);

}
