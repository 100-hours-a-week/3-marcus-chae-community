package kr.adapterz.springboot.config;

import kr.adapterz.springboot.post.entity.Post;
import kr.adapterz.springboot.post.repository.PostRepository;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Configuration
@Profile({"dev"})
public class DevDataSeeder {

    @Bean
    ApplicationRunner seed(UserRepository users, PostRepository posts) {
        return args -> seedData(users, posts);
    }

    @Transactional
    void seedData(UserRepository users, PostRepository posts) {
        if (posts.count() > 0) return;

        User u1 = users.findByEmail("alice@example.com")
                .orElseGet(() -> users.save(new User("alice@example.com", "{noop}password", "alice")));
        User u2 = users.findByEmail("bob@example.com")
                .orElseGet(() -> users.save(new User("bob@example.com", "{noop}password", "bob")));
        User u3 = users.findByEmail("carol@example.com")
                .orElseGet(() -> users.save(new User("carol@example.com", "{noop}password", "carol")));

        List<User> authors = List.of(u1, u2, u3);

        for (int i = 1; i <= 30; i++) {
            User author = authors.get((i - 1) % authors.size());
            Post p = new Post();
            p.setTitle("제목 " + i);
            p.setContent("내용 " + i + " - 더미 텍스트");
            p.setAuthor(author);
            posts.save(p);
        }
    }
}