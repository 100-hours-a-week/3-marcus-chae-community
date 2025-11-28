package kr.adapterz.springboot.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.dto.LoginRequest;
import kr.adapterz.springboot.auth.dto.LoginResponse;
import kr.adapterz.springboot.auth.dto.RefreshResponse;
import kr.adapterz.springboot.auth.exception.InvalidCredentialsException;
import kr.adapterz.springboot.auth.jwt.JwtProvider;
import kr.adapterz.springboot.auth.utils.AuthenticationExtractor;
import kr.adapterz.springboot.user.dto.MyProfileResponse;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtProvider.createAccessToken(user.getId());

        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        ResponseCookie cookie = createRefreshTokenCookie(refreshToken);

        return ResponseEntity.status(201)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponse(accessToken, MyProfileResponse.from(user)));
    }

    @DeleteMapping
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = deleteRefreshTokenCookie();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰 및 리프레시 토큰 발급
     * <p>
     * {@link kr.adapterz.springboot.auth.interceptor.JwtAuthInterceptor}에서
     * 이미 리프레시 토큰을 검증하고 userId를 추출했으므로,
     * 컨트롤러에서는 새 액세스 토큰과 리프레시 토큰 생성 및 반환
     * </p>
     * <p>
     * 리프레시 토큰 로테이션(Refresh Token Rotation) 방식으로,
     * 계속 사용하는 유저의 세션 자동 연장
     * </p>
     *
     * @param request HTTP 요청 (userId attribute 포함)
     * @return 새로 발급된 액세스 토큰
     */
    @GetMapping("/refresh")
    public ResponseEntity<RefreshResponse> refresh(HttpServletRequest request) {
        // 인터셉터에서 검증된 userId 추출
        Long userId = AuthenticationExtractor.extractUserIdFromHeader(request);

        // 새 액세스 토큰 생성
        String newAccessToken = jwtProvider.createAccessToken(userId);

        // 새 리프레시 토큰 생성 (세션 연장)
        String newRefreshToken = jwtProvider.createRefreshToken(userId);
        ResponseCookie cookie = createRefreshTokenCookie(newRefreshToken);

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new RefreshResponse(newAccessToken));
    }

    /**
     * Refresh Token 쿠키 생성
     * <p>
     * HttpOnly 플래그로 JavaScript 접근 차단하여 XSS 공격 방어
     *
     * @param token Refresh Token 값
     *
     * @return 생성된 ResponseCookie
     */
    private ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(AuthConstants.REFRESH_TOKEN_COOKIE_NAME, token)
                .path(AuthConstants.REFRESH_TOKEN_COOKIE_PATH)
                .httpOnly(true)
                .sameSite("None")
                .secure(!"local".equals(activeProfile))  // local은 HTTP, 그 외는 HTTPS 강제
                .maxAge(AuthConstants.REFRESH_TOKEN_MAX_AGE)
                .build();
    }

    /**
     * Refresh Token 쿠키 삭제
     * <p>
     * 로그아웃 시 사용. 빈 값 + maxAge(0)으로 브라우저에 즉시 만료 지시
     *
     * @return 삭제용 ResponseCookie
     */
    private ResponseCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(AuthConstants.REFRESH_TOKEN_COOKIE_NAME, "")
                .path(AuthConstants.REFRESH_TOKEN_COOKIE_PATH)
                .maxAge(0)
                .build();
    }
}
