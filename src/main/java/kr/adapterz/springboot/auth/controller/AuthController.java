package kr.adapterz.springboot.auth.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.dto.LoginRequest;
import kr.adapterz.springboot.auth.exception.InvalidCredentialsException;
import kr.adapterz.springboot.auth.session.SessionManager;
import kr.adapterz.springboot.auth.utils.PasswordUtils;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    public ResponseEntity<Void> login(@RequestBody @Valid LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(UserNotFoundException::new);

        if (!PasswordUtils.matches(user, req.password(), passwordEncoder)) {
            throw new InvalidCredentialsException();
        }

        String sessionId = sessionManager.createSession(user.getId()).getSessionId();

        // 응답에 넣을 Set-Cookie 값
        ResponseCookie cookie = ResponseCookie
                .from(AuthConstants.SESSION_COOKIE_NAME, sessionId)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(@CookieValue(AuthConstants.SESSION_COOKIE_NAME) String sessionId) {
        sessionManager.expire(sessionId);
        return ResponseEntity.ok().build();
    }
}
