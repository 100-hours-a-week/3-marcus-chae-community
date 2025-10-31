package kr.adapterz.springboot.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import kr.adapterz.springboot.auth.session.SessionManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    // 필터 제외 경로 목록
    private static final String[] EXCLUDED_PATHS = {
            "/auth", "/refresh", "/error"
    };
    private final SessionManager sessionManager;

    // 필터 제외 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath(); // context path 제외한 경로
        String method = request.getMethod();

        boolean isExcludedPath = Arrays.stream(EXCLUDED_PATHS).anyMatch(path::startsWith);
        boolean isUserPostRequest = path.startsWith("/users") && "POST".equals(method);

        return isExcludedPath || isUserPostRequest;
    }

    /**
     * {@link #shouldNotFilter(HttpServletRequest)}에서 허용한 경우를 제외하고 적용할 필터.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {

        try {
            // 요청에서 세션 ID 추출
            String sessionId = extractSessionId(request);

            // 세션 스토어에 저장돼있는지 검증, 없으면 예외 발생
            sessionManager.requireSession(sessionId);

            // 유효한 세션을 가진 요청임이 확인되었으므로 다음으로 진행
            chain.doFilter(request, response);
        } catch (UnauthenticatedException e) {
            // Filter에서 발생한 예외는 @RestControllerAdvice가 처리하지 못하므로 직접 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
        }
    }

    /**
     * 서블릿 요청의 쿠키로부터 세션 ID 추출하는 유틸 메서드<br>
     * fixme: 이 클래스 안에다가 놓는 게 옳은 것 같진 않지만 시간이 없어서 일단 넘어감.
     *
     * @param request {@link HttpServletRequest}
     *
     * @return 세션 ID
     *
     * @throws UnauthenticatedException 요청의 쿠키에 {@link AuthConstants#SESSION_COOKIE_NAME}이 없으면 예외 응답
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
}
