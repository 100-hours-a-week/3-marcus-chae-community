package kr.adapterz.springboot;

import jakarta.annotation.PostConstruct;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.entity.User;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InitialService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public String getMessage() {
        return "Hello, Adapterz!";
    }
}