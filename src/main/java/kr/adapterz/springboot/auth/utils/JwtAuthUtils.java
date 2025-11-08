package kr.adapterz.springboot.auth.utils;

import jakarta.servlet.http.HttpServletRequest;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import kr.adapterz.springboot.auth.filter.JwtAuthFilter;

/**
 * 세션 쿠키 처리 유틸리티
 */
public final class JwtAuthUtils {

    private JwtAuthUtils() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * HTTP 요청의 Authorization 헤더에서 JWT 토큰 추출
     *
     * @param request {@link HttpServletRequest}
     * @return JWT 토큰 문자열
     * @throws UnauthenticatedException Authorization 헤더가 없거나 형식이 잘못된 경우
     */
    public static String extractTokenFromHeader(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthenticatedException();
        }

        return authHeader.substring(7);
    }

    /**
     * HTTP 요청의 attribute로부터 userId 추출
     * <p>
     * {@link JwtAuthFilter}에서 인증된 사용자의 ID를
     * request attribute에 저장하므로, 이를 추출하여 반환.
     *
     * @param request {@link HttpServletRequest}
     * @return 인증된 사용자 ID
     * @throws UnauthenticatedException request attribute에 userId가 없는 경우
     */
    public static Long extractUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new UnauthenticatedException();
        }
        return userId;
    }
}
