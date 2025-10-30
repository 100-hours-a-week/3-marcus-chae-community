package kr.adapterz.springboot.config;


import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class SeedConfig {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner seedRunner() {
        return arguments -> seed(); // 부트 기동 후 1회 실행
    }

    @Transactional
    void seed() {
        if (userRepository.count() >= 10) return;

        // tester1 ~ tester10 계정 더미 데이터
        IntStream.rangeClosed(1, 10).forEach(i -> {
            String rawPassword = "123aS!" + i;
            String encodedPassword = passwordEncoder.encode(rawPassword);
            User user = new User("tester" + i + "@adapterz.kr", encodedPassword, "tester" + i);
            userRepository.save(user);
        });
    }
}