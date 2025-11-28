package kr.adapterz.springboot.auth.interceptor;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import kr.adapterz.springboot.auth.jwt.JwtProvider;
import kr.adapterz.springboot.auth.utils.AuthenticationExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 토큰 기반 인증 인터셉터
 * <p>
 * Spring MVC HandlerInterceptor를 사용하여 요청 인증 수행
 * 서블릿 필터와 달리 Spring 컨텍스트 내에서 동작하므로,
 * 발생한 예외는 {@link kr.adapterz.springboot.common.exception.GlobalExceptionHandler}에서 처리
 * </p>
 *
 * <h3>인증 플로우</h3>
 * <ul>
 *   <li>공개 경로 체크: 인증 없이 접근 가능한 경로는 바로 통과</li>
 *   <li>Refresh Token 처리: /auth/refresh 경로는 쿠키에서 토큰 추출</li>
 *   <li>Access Token 처리: 일반 요청은 Authorization 헤더에서 토큰 추출</li>
 *   <li>JWT 검증 및 userId 저장: 검증 성공 시 request attribute에 저장</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    /**
     * 컨트롤러 실행 전 JWT 인증 처리
     * <p>
     * 공개 경로를 제외한 모든 요청에 대해 JWT 토큰을 검증하고,
     * 검증된 사용자 ID를 request attribute에 저장
     * </p>
     *
     * @param request  HTTP 요청
     * @param response HTTP 응답
     * @param handler  실행될 핸들러
     * @return true: 컨트롤러로 진행, false: 요청 차단
     * @throws Exception 인증 실패 시 예외 발생 (GlobalExceptionHandler가 처리)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String path = request.getServletPath();
        String method = request.getMethod();

        // 1. 공개 경로 체크 (메서드별 구분)
        if (isPublicPath(path, method)) {
            return true;
        }

        // 2. Refresh Token 특수 처리 (쿠키 기반)
        if ("GET".equals(method) && "/auth/refresh".equals(path)) {
            String refreshToken = AuthenticationExtractor.extractTokenFromCookie(
                    request,
                    AuthConstants.REFRESH_TOKEN_COOKIE_NAME
            );
            Jws<Claims> jws = jwtProvider.parseToken(refreshToken);
            Long userId = extractUserIdFromClaims(jws.getBody());
            request.setAttribute(AuthConstants.USER_ID_ATTRIBUTE, userId);
            return true;
        }

        // 3. Access Token 처리 (헤더 기반)
        String accessToken = AuthenticationExtractor.extractTokenFromHeader(request);
        Jws<Claims> jws = jwtProvider.parseToken(accessToken);
        Long userId = extractUserIdFromClaims(jws.getBody());
        request.setAttribute(AuthConstants.USER_ID_ATTRIBUTE, userId);

        return true;
    }

    /**
     * 공개 경로 여부 판단
     * <p>
     * HTTP 메서드와 경로를 확인하여 인증 없이 접근 가능한지 판단
     * 필터의 shouldNotFilter() 로직과 동일하게 동작
     * </p>
     *
     * @param path   요청 경로
     * @param method HTTP 메서드
     * @return true: 공개 경로, false: 인증 필요
     */
    private boolean isPublicPath(String path, String method) {
        // CORS preflight 요청은 항상 허용
        if ("OPTIONS".equals(method)) {
            return true;
        }

        // 공개 페이지 및 개발 도구
        if (path.startsWith("/error")
                || path.equals("/privacy")
                || path.equals("/terms")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator")) {
            return true;
        }

        // HTTP 메서드, 경로별 공개 여부 판단
        return switch (method) {
            case "POST" -> path.equals("/auth") || path.startsWith("/users");
            case "GET" -> path.startsWith("/posts");
            default -> false;
        };
    }

    /**
     * JWT 클레임에서 검증된 사용자 ID를 추출
     *
     * @param claims JWT 클레임
     * @return 검증된 사용자 ID
     * @throws UnauthenticatedException subject 필드가 null인 경우
     */
    private Long extractUserIdFromClaims(Claims claims) {
        String subject = claims.getSubject();

        if (subject == null) {
            throw new UnauthenticatedException();
        }

        return Long.valueOf(subject);
    }
}