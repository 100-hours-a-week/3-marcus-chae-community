package kr.adapterz.springboot.user.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import kr.adapterz.springboot.user.dto.SignupRequest;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.EmailAlreadyExistsException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void signup(@Valid SignupRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException();
        }

        userRepository.save(new User(req.email(), req.password(), req.nickname()));
    }
}
