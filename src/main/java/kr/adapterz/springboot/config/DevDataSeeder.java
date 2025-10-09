package kr.adapterz.springboot.config;

import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Profile("dev")
@Component
public class DevDataSeeder implements CommandLineRunner {

    private final PostRepository postRepository;

    public DevDataSeeder(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // DB에 저장돼있는 post가 없으면 더미데이터 주입
    @Override
    public void run(String... args) {
        if (postRepository.count() == 0) {
            var posts = IntStream.rangeClosed(1, 10)
                    .mapToObj(i -> new Post("샘플 제목 " + i, "샘플 본문 " + i))
                    .toList();
            postRepository.saveAll(posts);
        }
    }
}