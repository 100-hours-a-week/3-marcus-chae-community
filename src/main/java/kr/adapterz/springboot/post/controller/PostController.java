package kr.adapterz.springboot.post.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.common.auth.CurrentUserProvider;
import kr.adapterz.springboot.post.dto.PostChunkResponse;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
import kr.adapterz.springboot.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CurrentUserProvider currentUserProvider;

    @PostMapping
    public ResponseEntity<PostDetailResponse> createPost(@RequestBody @Valid PostCreateRequest req) {
        Long authorId = currentUserProvider.requireId();
        PostDetailResponse res = postService.create(req, authorId);
        return ResponseEntity.status(201).body(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.status(200).body(postService.get(id));
    }

    @GetMapping
    public ResponseEntity<PostChunkResponse> getPostList(
            @RequestParam(name = "cursorCreatedAt", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime cursorCreatedAt,
            @RequestParam(name = "cursorId", required = false)
            Long cursorId
    ) {
        System.out.println("요청 받음 - cursorCreatedAt: " + cursorCreatedAt + ", cursorId: " + cursorId);

        PostChunkResponse res = postService.get10PostsUsingKeyset(cursorCreatedAt, cursorId);

        return ResponseEntity.status(200).body(res);
    }
}
