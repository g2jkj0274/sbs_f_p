package com.yk.Motivation.domain.qna.controller;

import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.qna.entity.Answer;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.qna.service.AnswerService;
import com.yk.Motivation.domain.qna.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/usr/qna/a")
@RequiredArgsConstructor
public class AnswerController {

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final Rq rq;

    @PostMapping("/create/{id}")
    public String createAnswer(@PathVariable("id") Long id, @RequestParam String content) {
        Member loginedMember = rq.getMember();
        Question question = this.questionService.getQuestion(id);

        Answer answer = new Answer();
        answer.setMember(loginedMember);
        answer.setContent(content);
        answer.setQuestion(question);

        RsData<Answer> saveRs = answerService.save(answer);
        return rq.redirectOrBack("/usr/qna/q/detail/%s".formatted(id), saveRs);
    }

    /*@PostMapping("/videoInCreateAnswer/{id}")
    public String videoInCreateAnswer(@PathVariable("id") Long id, @RequestParam String content, @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Member loginedMember = rq.getMember();
        Question question = this.questionService.getQuestion(id);

        Answer answer = new Answer();
        answer.setMember(loginedMember);
        answer.setContent(content);
        answer.setQuestion(question);

        RsData<Answer> saveRs = answerService.save(answer);
        return rq.redirectOrBack("/usr/qna/q/videoInDetail/%s?lessonId=%d".formatted(id, lessonId), saveRs);
    }*/
    @PostMapping("/videoInCreateAnswer/{id}")
    public String videoInCreateAnswer(@PathVariable("id") Long id,
                                      @RequestParam String content,
                                      @RequestParam String contentHtml,
                                      @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Member loginedMember = rq.getMember();
        Question question = this.questionService.getQuestion(id);

        Answer answer = new Answer();
        answer.setMember(loginedMember);
        answer.setContent(content); // 마크다운 내용 설정
        answer.setContentHtml(contentHtml);         // HTML 내용 설정
        answer.setQuestion(question);

        RsData<Answer> saveRs = answerService.save(answer);
        return rq.redirectOrBack("/usr/qna/q/videoInDetail/%s?lessonId=%d".formatted(id, lessonId), saveRs);
    }
}
