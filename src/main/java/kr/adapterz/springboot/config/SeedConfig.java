package kr.adapterz.springboot.config;


import kr.adapterz.springboot.comment.entity.Comment;
import kr.adapterz.springboot.comment.repository.CommentRepository;
import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 더미 데이터 생성기 (유저/게시글/댓글)
 */
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class SeedConfig {


    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner seedRunner() {
        return arguments -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        // 1) 유저 시드: tester1 ~ tester10
        if (userRepository.count() < 10) {
            IntStream.rangeClosed(1, 10).forEach(i -> {
                String rawPassword = "123456aS!" + i;
                String encodedPassword = passwordEncoder.encode(rawPassword);
                User user = new User("tester" + i + "@adapterz.kr", encodedPassword, "tester" + i);
                userRepository.save(user);
            });
        }

        // 사용자 목록 확보
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return; // 안전장치

        // 2) 게시글 시드: 30개 (제목 26자 제한 준수)
        if (postRepository.count() < 30) {
            List<Post> newPosts = new ArrayList<>();
            IntStream.rangeClosed(1, 30).forEach(i -> {
                User author = users.get((i - 1) % users.size());
                String title = String.format("Sample Post %02d", i); // 최대 20자 내외
                String content = "이것은 더미 게시글 내용입니다. 번호: " + i + "\n\nLorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sed.";
                newPosts.add(new Post(title, content, author));
            });
            postRepository.saveAll(newPosts);
        }

        // 3) 댓글 시드: 각 게시글당 3개씩
        long desiredComments = postRepository.count() * 3; // 포스트당 3개 목표
        if (commentRepository.count() < desiredComments) {
            List<Post> posts = postRepository.findAll();
            List<Comment> newComments = new ArrayList<>();
            int idx = 0;
            for (Post post : posts) {
                for (int j = 1; j <= 3; j++) {
                    User author = users.get(idx % users.size());
                    String content = switch (j) {
                        case 1 -> "첫 번째 댓글입니다!";
                        case 2 -> "좋은 글이네요 👍";
                        default -> "유익하게 읽었습니다.";
                    };
                    newComments.add(new Comment(author, post, content));
                    idx++;
                }
            }
            if (!newComments.isEmpty()) {
                commentRepository.saveAll(newComments);
            }
        }
    }
}