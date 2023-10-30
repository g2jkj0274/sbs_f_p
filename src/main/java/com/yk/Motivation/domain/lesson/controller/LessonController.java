package com.yk.Motivation.domain.lesson.controller;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rq.Rq;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.controller.ArticleController;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.lecture.controller.LectureController;
import com.yk.Motivation.domain.lecture.entity.Lecture;
import com.yk.Motivation.domain.lecture.service.LectureService;
import com.yk.Motivation.domain.lesson.entity.Lesson;
import com.yk.Motivation.domain.lesson.service.LessonService;
import com.yk.Motivation.standard.util.Ut;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/usr/lesson")
@RequiredArgsConstructor
@Validated
public class LessonController {

    private final LessonService lessonService;
    private final LectureService lectureService;
    private final GenFileService genFileService;
    private final Rq rq;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{lectureId}/write")
    public String showWrite(
            Model model,
            @PathVariable Long lectureId
    ) {
        Lecture lecture = lectureService.findById(lectureId).get();

        model.addAttribute("lecture", lecture);

        return "usr/lesson/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{lectureId}/write")
    @ResponseBody
    public RsData<Long> write(
            @PathVariable Long lectureId,
            @Valid LessonController.LessonWriteForm writeForm
    ) {
        Lecture lecture = lectureService.findById(lectureId).get();

        RsData<Lecture> rsData = lessonService.write(lecture, writeForm.getSubjects(), writeForm.getVideos());

        return RsData.of("S-1", "%d 번 강의가 등록 되었습니다.".formatted(lectureId), lecture.getId());
    }

    @Setter
    @Getter
    public static class LessonWriteForm {
        private List<String> subjects;
        private List<MultipartFile> videos;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{lectureId}/modify")
    public String showModify(
            Model model,
            @PathVariable long lectureId
    ) {
        Lecture lecture = lectureService.findById(lectureId).get();
        List<Lesson> lessons = lecture.getLessons();

        model.addAttribute("lecture", lecture);
        model.addAttribute("lessons", lessons);

        return "usr/lesson/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{lectureId}/modify")
    @ResponseBody
    public RsData<Long> modify(
            @PathVariable Long lectureId,
            @Valid LessonController.LessonModifyForm modifyForm
    ) {

        Lecture lecture = lectureService.findById(lectureId).get();

        lessonService.modify(lecture, modifyForm.getSubject(), modifyForm.getVideo());

        return RsData.of("S-1", "%d 번 강의가 수정 되었습니다.".formatted(lectureId), lecture.getId());
    }

    @Getter
    @Setter
    public static class LessonModifyForm {
        private List<String> subject;
        private List<MultipartFile> video;
    }





    @GetMapping("/hls/{lessonId}")
    public String videoHls(
            Model model,
            @PathVariable long lessonId
            ) {

        String masterPlayListPath = getHlsSourcePath(lessonId, "master.m3u8");

        model.addAttribute("videoUrl", masterPlayListPath);
        return "usr/lesson/hls";
    }

    private String getHlsSourcePath(long lessonId, String fileName) {
        Lesson lesson = lessonService.findById(lessonId).get();
        GenFile genFile = genFileService.findBy(lesson.getModelName(), lesson.getId(), "common", "lessonVideo", 1).get();

        return "/gen/" + genFile.getFileDir() + "/" + "hls" + "/" + fileName;
    }


}
