package kr.adapterz.springboot.user.service;

import kr.adapterz.springboot.auth.utils.PasswordUtils;
import kr.adapterz.springboot.user.dto.EditNicknameRequest;
import kr.adapterz.springboot.user.dto.EditPasswordRequest;
import kr.adapterz.springboot.user.dto.SignupRequest;
import kr.adapterz.springboot.user.dto.MyProfileResponse;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.EmailAlreadyExistsException;
import kr.adapterz.springboot.user.exception.InvalidPasswordException;
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
    public void signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException();
        }

        if (userRepository.existsByNickname(request.nickname())) {
            throw new NicknameAlreadyExistsException();
        }

        userRepository.save(new User(request.email(), passwordEncoder.encode(request.password()), request.nickname()));
    }

    @Transactional(readOnly = true)
    public MyProfileResponse getUserDetail(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return new MyProfileResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public MyProfileResponse editNickname(Long userId, EditNicknameRequest request) {
        // 닉네임 중복 체크
        if (userRepository.existsByNickname(request.newNickname())) {
            throw new NicknameAlreadyExistsException();
        }

        // 사용자 조회 및 닉네임 변경
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        user.setNickname(request.newNickname());

        return new MyProfileResponse(user.getId(), user.getEmail(), user.getNickname());
    }

    @Transactional
    public void editPassword(Long userId, EditPasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!PasswordUtils.matches(user, request.originalPassword(), passwordEncoder)) {
            throw new InvalidPasswordException();
        }

        String encodedPassword = passwordEncoder.encode(request.newPassword());
        user.setPassword(encodedPassword);

        userRepository.save(user);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }
}
