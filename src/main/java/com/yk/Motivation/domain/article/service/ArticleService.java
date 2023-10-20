package com.yk.Motivation.domain.article.service;

import com.yk.Motivation.base.app.AppConfig;
import com.yk.Motivation.base.rsData.RsData;
import com.yk.Motivation.domain.article.entity.Article;
import com.yk.Motivation.domain.article.repository.ArticleRepository;
import com.yk.Motivation.domain.board.entity.Board;
import com.yk.Motivation.domain.genFile.entity.GenFile;
import com.yk.Motivation.domain.genFile.service.GenFileService;
import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final GenFileService genFileService;

    @Transactional
    public RsData<Article> write(Board board, Member author, String subject, String body) {
        Article article = Article.builder()
                .board(board)
                .author(author)
                .subject(subject)
                .body(body)
                .build();

        articleRepository.save(article);

        return new RsData<>("S-1", article.getId() + "번 게시물이 생성되었습니다.", article);
    }

    public Page<Article> findByKw(Board board, String kwType, String kw, Pageable pageable) {
        return articleRepository.findByKw(board, kwType, kw, pageable);
    }

    public Optional<Article> findById(long id) {
        return articleRepository.findById(id);
    }

    public RsData<?> checkActorCanModify(Member actor, Article article) {
        if (actor == null || !actor.equals(article.getAuthor())) {
            return new RsData<>("F-1", "권한이 없습니다.", null);
        }

        return new RsData<>("S-1", "가능합니다.", null);
    }

    public RsData<?> checkActorCanDelete(Member actor, Article article) {
        return checkActorCanModify(actor, article);
    }

    @Transactional
    public RsData<Article> modify(Article article, String subject, String body) {
        article.setSubject(subject);
        article.setBody(body);

        return new RsData<>("S-1", article.getId() + "번 게시물이 수정되었습니다.", article);
    }

    @Transactional
    public RsData<?> remove(Article article) {
        findGenFiles(article).forEach(genFileService::remove);

        articleRepository.delete(article);

        return new RsData<>("S-1", article.getId() + "번 게시물이 삭제되었습니다.", null);
    }

    private List<GenFile> findGenFiles(Article article) {
        return genFileService.findByRelId(article.getModelName(), article.getId());
    }

    @Transactional
    public RsData<GenFile> saveAttachmentFile(Article article, MultipartFile attachmentFile, int fileNo) {
        String attachmentFilePath = Ut.file.toFile(attachmentFile, AppConfig.getTempDirPath());
        return saveAttachmentFile(article, attachmentFilePath, fileNo);
    }

    @Transactional
    public RsData<GenFile> saveAttachmentFile(Article article, String attachmentFile, int fileNo) {
        GenFile genFile = genFileService.save(article.getModelName(), article.getId(), "common", "attachment", fileNo, attachmentFile);

        return new RsData<>("S-1", genFile.getId() + "번 파일이 생성되었습니다.", genFile);
    }

    public Map<String, GenFile> findGenFilesMapKeyByFileNo(Article article, String typeCode, String type2Code) {
        return genFileService.findGenFilesMapKeyByFileNo(article.getModelName(), article.getId(), typeCode, type2Code);
    }

    @Transactional
    public void removeAttachmentFile(Article article, int fileNo) {
        genFileService.remove(article.getModelName(), article.getId(), "common", "attachment", fileNo);
    }
}