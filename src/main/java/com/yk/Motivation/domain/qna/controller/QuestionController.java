package com.yk.Motivation.domain.qna.controller;

import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.comment.entity.Comment;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.service.LessonService;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.qna.entity.Question;
import com.yk.Motivation.domain.qna.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usr/qna/q")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final Rq rq;
    private final LessonService lessonService;

    @GetMapping("/list")
    public String list(Model model, @RequestParam(defaultValue = "1") int page, @PageableDefault(size = 10) Pageable pageable) {
        // 페이지 번호를 0 기반 인덱스로 변환 (1 페이지 -> 0, 2 페이지 -> 1, ...)
        int zeroBasedPage = Math.max(0, page - 1);

        // 최신 순으로 정렬하기 위한 Sort 객체 생성
        Sort sort = Sort.by(Sort.Direction.DESC, "createDate");

        // PageRequest 객체 생성 (정렬 조건 포함)
        Pageable adjustedPageable = PageRequest.of(zeroBasedPage, pageable.getPageSize(), sort);

        Member currentMember = rq.getMember();
        Page<Question> questionPage = this.questionService.getList(adjustedPageable);
        List<Boolean> hasAnswersByOthersList = questionPage.getContent().stream()
                .map(question -> this.questionService.hasAnswersByOthers(question, currentMember))
                .collect(Collectors.toList());

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("hasAnswersByOthersList", hasAnswersByOthersList);
        return "usr/qna/list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, @PathVariable("id") Long id) {
        Question question = this.questionService.getQuestion(id);
        // 조회수 증가
        question.setViewCount(question.getViewCount() + 1);
        questionService.save(question); // 증가된 조회수를 저장
        model.addAttribute("question", question);
        return "usr/qna/detail";
    }

    // 질문 작성 페이지 뷰
    @GetMapping("/create")
    public String create(Model model) {
        return "usr/qna/create";
    }
    // 질문 작성 데이터 처리
    @PostMapping("/create")
    public String handleCreate(Question question) {
        Member loginedMember = rq.getMember();

        if(loginedMember == null) {
            return "redirect:/login";
        }

        question.setMember(loginedMember); // 로그인된 멤버 정보를 Question 객체에 설정
        RsData<Question> writeRs = questionService.create(question);
        return rq.redirectOrBack("/usr/qna/q/list", writeRs);
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        Question question = questionService.findById(id).get();
        RsData<?> deleteRs = questionService.remove(question);
        return rq.redirectOrBack("/usr/qna/q/list", deleteRs);
    }

    @GetMapping("/modify/{id}")
    public String modify(@PathVariable Long id, Model model) {
        Optional<Question> questionOptional = questionService.findById(id);
        if (!questionOptional.isPresent()) {
            return "redirect:/usr/qna/q/list";
        }
        model.addAttribute("question", questionOptional.get());
        return "usr/qna/modify";
    }
    @PostMapping("/modify/{id}")
    public String modifyQuestion(@PathVariable Long id, @ModelAttribute Question question, RedirectAttributes redirectAttributes) {
        Optional<Question> existingQuestion = questionService.findById(id);

        if (!existingQuestion.isPresent()) {
            return "usr/qna/modify";
        }

        Question updatedQuestion = existingQuestion.get();
        updatedQuestion.setSubject(question.getSubject());
        updatedQuestion.setBody(question.getBody());
        updatedQuestion.setBodyHtml(question.getBodyHtml());

        RsData<Question> modifyRs = questionService.save(updatedQuestion);
        return rq.redirectOrBack("/usr/qna/q/detail/" + updatedQuestion.getId(), modifyRs);
    }

    /*####################################################################################################################################*/
    /*####################################################################################################################################*/
    /*####################################################################################################################################*/

    // 비디오 페이지 안의 QnA List
    @GetMapping("/videoInList/{lessonId}")
    public String videoInList(Model model, @PathVariable long lessonId, @PageableDefault(size = 7) Pageable pageable) {
        Member currentMember = rq.getMember();
        Lesson lesson = lessonService.findById(lessonId).orElse(null);

        // 레슨에서 강의 정보를 가져옵니다.
        Lecture lecture = lesson.getLecture();
        long lectureId = lecture.getId();

        // 모델에 강의 ID와 레슨 ID를 추가합니다.
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("lessonId", lessonId);

        Page<Question> questionPage = this.questionService.getListWithPriority(lectureId, lessonId, currentMember, pageable);

        List<Boolean> hasAnswersByOthersList = questionPage.getContent().stream()
                .map(question -> this.questionService.hasAnswersByOthers(question, currentMember))
                .collect(Collectors.toList());

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("hasAnswersByOthersList", hasAnswersByOthersList);

        return "usr/qna/videoInList";
    }

    @GetMapping(value = "/videoInDetail/{id}")
    public String videoInDetail(Model model, @PathVariable("id") Long id, @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Question question = this.questionService.getQuestion(id);
        // 조회수 증가
        question.setViewCount(question.getViewCount() + 1);
        questionService.save(question); // 증가된 조회수를 저장
        model.addAttribute("question", question);

        Lesson lesson = lessonService.findById(lessonId).orElse(null);

        // 레슨에서 강의 정보를 가져옵니다.
        Lecture lecture = lesson.getLecture();
        long lectureId = lecture.getId();
        // 모델에 강의 ID와 레슨 ID를 추가합니다.
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("lessonId", lessonId);

        // lessonId가 제공되었다면 모델에 추가
        if (lessonId != null) {
            model.addAttribute("lessonId", lessonId);
        }

        return "usr/qna/videoInDetail";
    }

    @GetMapping("/videoInCreate")
    public String videoInCreate(
            @RequestParam(required = false) Long lectureId,
            @RequestParam(required = false) Long lessonId,
            Model model) {

        if (lectureId != null) model.addAttribute("lectureId", lectureId);
        if (lessonId != null) model.addAttribute("lessonId", lessonId);

        return "usr/qna/videoInCreate";
    }
    @PostMapping("/videoInCreate")
    public String videoInHandleCreate(Question question, @RequestParam("lesson") long lessonId) {
        Member loginedMember = rq.getMember();

        if(loginedMember == null) {
            return "redirect:/login";
        }

        question.setMember(loginedMember); // 로그인된 멤버 정보를 Question 객체에 설정
        questionService.create(question);  // 데이터베이스에 저장하는 로직

        // lessonId를 URL에 포함하여 리다이렉션

        RsData<Question> writeRs = questionService.create(question);
        return rq.redirectOrBack("/usr/qna/q/videoInList/" + lessonId, writeRs);
    }

    @GetMapping("/videoInDelete/{id}")
    public String videoInDelete(@PathVariable Long id, @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Question question = questionService.findById(id).get();
        RsData<?> deleteRs = questionService.remove(question);
        return rq.redirectOrBack("/usr/qna/q/videoInList/" + (lessonId != null ? lessonId : "0"), deleteRs);
    }

    @GetMapping("/videoInModify/{id}")
    public String videoInModify(@PathVariable Long id, Model model, @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Optional<Question> questionOptional = questionService.findById(id);
        if (!questionOptional.isPresent()) {
            return "redirect:/usr/qna/q/videoInList/" + (lessonId != null ? lessonId : "0");
        }
        if (lessonId != null) model.addAttribute("lessonId", lessonId);
        model.addAttribute("question", questionOptional.get());
        return "usr/qna/videoInModify";
    }
    @PostMapping("/videoInModify/{id}")
    public String videoInModifyQuestion(@PathVariable Long id,
                                        @ModelAttribute Question question,
                                        RedirectAttributes redirectAttributes,
                                        @RequestParam(name = "lessonId", required = false) Long lessonId) {
        Optional<Question> existingQuestion = questionService.findById(id);

        if (!existingQuestion.isPresent()) {
            return "usr/qna/videoInModify";
        }

        Question updatedQuestion = existingQuestion.get();
        updatedQuestion.setSubject(question.getSubject());
        updatedQuestion.setBody(question.getBody());
        updatedQuestion.setBodyHtml(question.getBodyHtml());

        RsData<Question> modifyRs = questionService.save(updatedQuestion);
        return rq.redirectOrBack("/usr/qna/q/videoInDetail/" + updatedQuestion.getId() + "?lessonId=" + (lessonId != null ? lessonId : "0"), modifyRs);
    }
}
