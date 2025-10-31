package kr.adapterz.springboot.user.service;

import kr.adapterz.springboot.user.dto.SignupRequest;
import kr.adapterz.springboot.user.dto.UserDetailResponse;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.EmailAlreadyExistsException;
import kr.adapterz.springboot.user.exception.NicknameAlreadyExistsException;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequest req) {

        if (userRepository.existsByEmail(req.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (userRepository.existsByNickname(req.nickname())) {
            throw new NicknameAlreadyExistsException();
        }

        userRepository.save(new User(req.email(), passwordEncoder.encode(req.password()), req.nickname()));
    }

    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        return new UserDetailResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
