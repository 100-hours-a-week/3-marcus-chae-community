package kr.adapterz.springboot.post.service;


import kr.adapterz.springboot.post.dto.PostChunkResponse;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.exception.PostNotFoundException;
import kr.adapterz.springboot.post.mapper.PostMapper;
import kr.adapterz.springboot.post.repository.PostRepository;
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
    private final PostMapper postMapper;

    @Transactional
    public PostDetailResponse create(PostCreateRequest req, Long authorId) {
        Post post = postMapper.toEntity(req);
        // req, author 둘 다 mapstruct에게 인자로 주려면 직접 로직을 작성해야하므로 자동 생성의 이점을 살리기 위해 그냥 setter로 주입
        post.setAuthor(userRepository.getReferenceById(authorId)); // fixme: author는 변경되면 안되기 땜에 세터 써선 안되는 부분. 생성자나 팩토리 메서드 쓰도록.
        Post saved = postRepository.save(post);
        return postMapper.toDetailResponse(saved);
    }

    @Transactional(readOnly = true)
    public PostDetailResponse get(Long id) {
        return postRepository.findById(id)
                .map(postMapper::toDetailResponse)
                .orElseThrow(PostNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public PostChunkResponse get10PostsUsingKeyset(LocalDateTime createdAt, Long id) {
        ScrollPosition position;
        if (createdAt == null && id == null) {
            // 처음 요청: 맨 처음부터 조회
            position = ScrollPosition.keyset();
        } else {
            // cursor 있음: 해당 시점 이후 조회
            position = ScrollPosition.forward(Map.of("createdAt", createdAt, "id", id));
        }

        Window<Post> window = postRepository.findFirst10ByOrderByCreatedAtDesc(position);

        // 게시글에서 요약정보만 추출
        List<PostChunkResponse.PostSummary> summaries = window.getContent().stream()
                .map(PostChunkResponse.PostSummary::from)
                .toList();

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
