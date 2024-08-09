package com.springbootstudy.sociallogin.controller;

import com.springbootstudy.sociallogin.dto.PostDto;
import com.springbootstudy.sociallogin.dto.PostsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @GetMapping
    public ResponseEntity<PostsDto> getPosts() {
        List<PostDto> postDtos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UUID uuid = UUID.randomUUID();
            postDtos.add(new PostDto(i, "title" + i + uuid, "content" + i));
        }

        return ResponseEntity.ok(new PostsDto(postDtos));
    }
}
