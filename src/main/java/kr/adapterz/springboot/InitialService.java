package kr.adapterz.springboot;

import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitialService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public String getMessage() {
        return "Hello, Adapterz!";
    }
}