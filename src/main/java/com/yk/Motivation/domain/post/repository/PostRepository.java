package com.yk.Motivation.domain.post.repository;

import com.yk.Motivation.domain.member.entity.Member;
import com.yk.Motivation.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    Page<Post> findByPostTags_content(String tagContent, Pageable pageable);

    Page<Post> findByAuthorAndPostTags_content(Member author, String tagContent, Pageable pageable);
}