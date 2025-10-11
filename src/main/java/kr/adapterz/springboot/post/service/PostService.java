package kr.adapterz.springboot.post.service;

import jakarta.transaction.Transactional;
import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.mapper.PostMapper;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostDetailResponse getPost(Long id) {
        return postRepository.findById(id)
                .map(p -> new PostDetailResponse(p.getId(), p.getTitle(), p.getContent(), p.getCreatedAt()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 게시글은 존재하지 않습니다."));
    }

    @Transactional
    public PostDetailResponse create(PostCreateRequest req, Long authorId) {
        Post post = postMapper.toEntity(req);
        // req, author 둘 다 mapstruct에게 인자로 주려면 직접 로직을 작성해야하므로 자동 생성의 이점을 살리기 위해 그냥 setter로 주입
        post.setAuthor(userRepository.getReferenceById(authorId));
        Post saved = postRepository.save(post);
        return postMapper.toDetailResponse(saved);
    }
}
