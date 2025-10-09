package kr.adapterz.springboot.post.service;

import kr.adapterz.springboot.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public PostDetailDto getPost(Long id) {
        return postRepository.findById(id)
                .map(p -> new PostDetailDto(p.getId(), p.getTitle(), p.getContent()))
                .orElseThrow(() -> new IllegalArgumentException("Post %d not found".formatted(id)));
    }

    public static record PostDetailDto(Long id, String title, String content) {}
}
