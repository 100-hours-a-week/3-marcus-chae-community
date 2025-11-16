package kr.adapterz.springboot.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;

/**
 * 인증 정보 추출 유틸리티
 * <p>
 * HTTP 요청에서 JWT 토큰 및 사용자 ID를 추출
 */
public final class AuthenticationExtractor {

    private AuthenticationExtractor() {
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
     * {@link kr.adapterz.springboot.auth.interceptor.JwtAuthInterceptor}에서 인증된 사용자의 ID를
     * request attribute에 저장하므로, 이를 추출하여 반환.
     *
     * @param request {@link HttpServletRequest}
     * @return 인증된 사용자 ID
     * @throws UnauthenticatedException request attribute에 userId가 없는 경우
     */
    public static Long extractUserIdFromHeader(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute(AuthConstants.USER_ID_ATTRIBUTE);
        if (userId == null) {
            throw new UnauthenticatedException();
        }
        return userId;
    }

    /**
     * HTTP 요청의 쿠키에서 특정 이름의 쿠키값 추출
     *
     * @param request {@link HttpServletRequest}
     * @param cookieName 추출할 쿠키 이름
     * @return 쿠키값 문자열
     * @throws UnauthenticatedException 해당 이름의 쿠키가 없는 경우
     */
    public static String extractTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new UnauthenticatedException();
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new UnauthenticatedException();
    }
}
