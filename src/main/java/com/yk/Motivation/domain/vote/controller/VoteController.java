package com.yk.Motivation.domain.vote.controller;

import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.vote.entity.Vote;
import com.yk.Motivation.domain.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping("/usr/vote/add")
    public String toggleVote(@RequestParam String nickName, @RequestParam Long article) {
        if (voteService.hasAlreadyVoted(nickName, article)) {
            voteService.cancelVote(nickName, article);
        } else {
            Vote vote = Vote.builder()
                    .nickName(nickName)
                    .article(Article.builder().id(article).build())
                    .build();

            voteService.addVote(vote);
        }

        return "redirect:/usr/article/free1/detail/" + article;
    }
}
