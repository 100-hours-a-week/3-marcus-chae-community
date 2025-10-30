package kr.adapterz.springboot.auth.service;

import kr.adapterz.springboot.auth.dto.LoginRequest;
import kr.adapterz.springboot.auth.exception.InvalidCredentialsException;
import kr.adapterz.springboot.auth.session.Session;
import kr.adapterz.springboot.auth.session.SessionManager;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final SessionManager sessionManager; // consider: 생각해보니 세션매니저 여기서 쓰면 안됨. 컨트롤러가 써야됨. 요청한 사용자를 식별하는 건 서비스가 아니라 요청을 직접 받는 컨트롤러의 몫이며 컨트롤러만 할 수 있음.
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * 로그인 요청에 대해 대응되는 {@link User}를 {@link UserRepository}에서 조회함 {@link Session}을 생성하여 {@link SessionManager#sessionStore}에 저장해주고 세션ID를 반환
     */
    public String login(LoginRequest req) {
        String email = req.email();
        String rawPassword = req.password();

        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if (!checkPassword(user, rawPassword)) {
            throw new InvalidCredentialsException();
        }

        // 로그인
        Session session = sessionManager.createSession(user.getId());
        return session.getSessionId();
    }

    public void logout(String sessionId) {
        sessionManager.expire(sessionId);
    }

    private boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
