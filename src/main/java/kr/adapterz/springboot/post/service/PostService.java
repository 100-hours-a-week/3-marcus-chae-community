package kr.adapterz.springboot.post.service;


import kr.adapterz.springboot.post.dto.PostChunkResponse;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
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

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public PostDetailResponse create(PostCreateRequest req, Long authorId) {
        User author = userRepository.getReferenceById(authorId);
        Post post = new Post(req.title(), req.content(), author);
        Post saved = postRepository.save(post);

        return PostDetailResponse.from(post);
    }

    /**
     * 게시글 상세 조회
     *
     * @param postId
     *
     * @return
     */
    @Transactional(readOnly = true)
    public PostDetailResponse get(Long postId) {
        Post post = postRepository.findWithAuthorById(postId).orElseThrow(PostNotFoundException::new);

        return PostDetailResponse.from(post);
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
}
