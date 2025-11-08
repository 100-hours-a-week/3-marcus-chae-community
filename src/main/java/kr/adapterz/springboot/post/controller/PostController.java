package kr.adapterz.springboot.post.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.utils.JwtAuthUtils;
import kr.adapterz.springboot.post.dto.PostChunkResponse;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostResponse;
import kr.adapterz.springboot.post.dto.PostUpdateRequest;
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

    @PostMapping
    public ResponseEntity<PostResponse> create(
            HttpServletRequest request,
            @RequestBody @Valid PostCreateRequest body
    ) {
        Long userId = JwtAuthUtils.extractUserId(request);
        PostResponse response = postService.create(body, userId);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> get(@PathVariable Long id) {
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

    @PatchMapping("/{id}")
    public ResponseEntity<PostResponse> update(
            HttpServletRequest request,
            @PathVariable(name = "id") Long postId,
            @RequestBody @Valid PostUpdateRequest body
    ) {
        Long userId = JwtAuthUtils.extractUserId(request);
        PostResponse response = postService.update(userId, postId, body);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            HttpServletRequest request,
            @PathVariable(name = "id") Long postId
    ) {
        Long userId = JwtAuthUtils.extractUserId(request);
        postService.delete(userId, postId);
        return ResponseEntity.noContent().build();
    }
}
