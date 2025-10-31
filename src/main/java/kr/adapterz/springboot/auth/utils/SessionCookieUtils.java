package kr.adapterz.springboot.auth.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;

import java.util.Arrays;

/**
 * 세션 쿠키 처리 유틸리티
 */
public final class SessionCookieUtils {

    private SessionCookieUtils() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * HTTP 요청의 쿠키로부터 세션 ID 추출
     *
     * @param request {@link HttpServletRequest}
     * @return 세션 ID
     * @throws UnauthenticatedException 요청의 쿠키에 {@link AuthConstants#SESSION_COOKIE_NAME}이 없으면 예외 발생
     */
    public static String extractSessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new UnauthenticatedException();
        }

        return Arrays.stream(cookies)
                .filter(c -> AuthConstants.SESSION_COOKIE_NAME.equals(c.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(UnauthenticatedException::new);
    }

    /**
     * HTTP 요청의 attribute로부터 userId 추출
     * <p>
     * {@link kr.adapterz.springboot.auth.filter.SessionAuthFilter}에서 인증된 사용자의 ID를
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
