package kr.adapterz.springboot.post.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.common.auth.CurrentUserProvider;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
import kr.adapterz.springboot.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final CurrentUserProvider currentUserProvider;

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailResponse> getPost(@PathVariable Long id) {
        return ResponseEntity.status(200).body(postService.getPost(id));
    }

    @PostMapping
    public ResponseEntity<PostDetailResponse> createPost(@RequestBody @Valid PostCreateRequest req) {
        Long authorId = currentUserProvider.requireId();
        PostDetailResponse res = postService.create(req, authorId);
        return ResponseEntity.status(201).body(res);
    }
}
