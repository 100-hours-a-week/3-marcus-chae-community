package kr.adapterz.springboot.post.service;


import kr.adapterz.springboot.auth.exception.UnauthorizedException;
import kr.adapterz.springboot.comment.dto.CommentResponse;
import kr.adapterz.springboot.comment.service.CommentService;
import kr.adapterz.springboot.post.dto.PostChunkResponse;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostResponse;
import kr.adapterz.springboot.post.dto.PostUpdateRequest;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.exception.PostNotFoundException;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PostService {
    private final CommentService commentService;

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse create(PostCreateRequest req, Long authorId) {
        User author = userRepository.getReferenceById(authorId);
        Post post = new Post(req.title(), req.content(), author);
        Post saved = postRepository.save(post);

        return PostResponse.from(post);
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId
     *
     * @return
     */
    @Transactional
    public PostResponse get(Long postId) {
        Post post = postRepository.findWithAuthorById(postId).orElseThrow(PostNotFoundException::new);

        // 조회수 증가 (벌크 업데이트)
        postRepository.incrementViewCount(postId);

        // 메모리의 엔티티도 동기화
        post.incrementViewCount();

        List<CommentResponse> comments = commentService.findByPostId(post.getId());

        return PostResponse.from(post, comments);
    }

    @Transactional(readOnly = true)
    public PostChunkResponse get10PostsUsingKeyset(LocalDateTime createdAt, Long postId) {
        ScrollPosition position;
        if (createdAt == null && postId == null) {
            // 처음 요청: 맨 처음부터 조회
            position = ScrollPosition.keyset();
        } else {
            // cursor 있음: 해당 시점 이후 조회
            position = ScrollPosition.forward(Map.of("createdAt", createdAt, "id", postId));
        }

        Window<Post> window = postRepository.findFirst10ByOrderByCreatedAtDesc(position);

        // 게시글에서 요약정보만 추출
        List<PostChunkResponse.PostSummary> summaries = window.getContent().stream().map(PostChunkResponse.PostSummary::from).toList();

        // 다음 cursor 정보
        PostChunkResponse.CursorInfo cursorInfo;
        if (window.hasNext() && !window.isEmpty()) {
            // 마지막 항목의 cursor
            Post lastPost = window.getContent().getLast();
            cursorInfo = new PostChunkResponse.CursorInfo(lastPost.getCreatedAt(), lastPost.getId(), true);
        } else {
            cursorInfo = new PostChunkResponse.CursorInfo(null, null, false);
        }

        return new PostChunkResponse(summaries, cursorInfo);
    }

    @Transactional
    public PostResponse update(Long requestingUserId, Long targetPostId, PostUpdateRequest request) {
        // 요청 유저가 대상 게시글의 작성자와 일치하는지 확인
        Post targetPost = postRepository.findById(targetPostId).orElseThrow(PostNotFoundException::new);
        if (!targetPost.getAuthor().getId().equals(requestingUserId)) {
            throw new UnauthorizedException();
        }

        // 부분 업데이트
        if (request.title() != null) {
            targetPost.setTitle(request.title());
        }
        if (request.content() != null) {
            targetPost.setContent(request.content());
        }

        Post savedPost = postRepository.save(targetPost);

        return PostResponse.from(savedPost);
    }

    @Transactional
    public void delete(Long requestingUserId, Long targetPostId) {
        Post targetPost = postRepository.findById(targetPostId).orElseThrow(PostNotFoundException::new);
        if (!targetPost.getAuthor().getId().equals(requestingUserId)) {
            throw new UnauthorizedException();
        }

        postRepository.deleteById(targetPostId);
    }
}
