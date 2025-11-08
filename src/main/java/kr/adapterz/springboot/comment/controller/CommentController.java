package kr.adapterz.springboot.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.utils.JwtAuthUtils;
import kr.adapterz.springboot.comment.dto.CommentCreateRequest;
import kr.adapterz.springboot.comment.dto.CommentResponse;
import kr.adapterz.springboot.comment.dto.CommentUpdateRequest;
import kr.adapterz.springboot.comment.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
// 댓글 ID가 아직 부여되지 않았을 때의 요청은 /comments 경로로 시작하지 않기 때문에 공통 @RequestMapping()을 선언하지 않았음.
class CommentController {
    private final CommentService commentService;

    CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments") // /posts로 경로가 시작하니까 PostController에 위치해야하려나 고민해보았는데, 경로보다 도메인별로 분류하는 게 더 본질적인 분류 방식이라고 판단.
    public ResponseEntity<CommentResponse> create(HttpServletRequest request, @PathVariable Long postId, @RequestBody @Valid CommentCreateRequest body) {
        Long authorId = JwtAuthUtils.extractUserId(request);
        CommentResponse response = commentService.create(authorId, postId, body);
        return ResponseEntity.status(201).body(response);
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> update(HttpServletRequest request, @RequestBody @Valid CommentUpdateRequest body, @PathVariable Long id) {
        Long requestorId = JwtAuthUtils.extractUserId(request);
        CommentResponse response = commentService.update(requestorId, id, body);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> delete(HttpServletRequest request, @PathVariable Long id) {
        Long requestorId = JwtAuthUtils.extractUserId(request);
        commentService.delete(requestorId,id);

        return ResponseEntity.noContent().build();
    }
}
