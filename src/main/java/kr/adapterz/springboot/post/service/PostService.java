package kr.adapterz.springboot.post.service;


import kr.adapterz.springboot.post.dto.PostCreateRequest;
import kr.adapterz.springboot.post.dto.PostDetailResponse;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.exception.PostNotFoundException;
import kr.adapterz.springboot.post.mapper.PostMapper;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
