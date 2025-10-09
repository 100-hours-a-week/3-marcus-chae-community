package kr.adapterz.springboot.user.service;

import kr.adapterz.springboot.entity.User;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // fixme: 실습용으로 막 쓴 메서드
    @Transactional(readOnly = true)
    public User getBasicById(Long id) {
        Optional<User> u = userRepository.findById(id);
        return u.orElseThrow();
    }
}
