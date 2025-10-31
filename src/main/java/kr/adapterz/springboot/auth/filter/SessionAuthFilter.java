package kr.adapterz.springboot.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import kr.adapterz.springboot.auth.session.SessionManager;
import kr.adapterz.springboot.auth.utils.SessionCookieUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SessionAuthFilter extends OncePerRequestFilter {

    private final SessionManager sessionManager;

    // 필터 제외 경로 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath(); // context path 제외한 경로
        String method = request.getMethod();

        // /auth는 POST(로그인)만 제외, DELETE(로그아웃)는 인증 필요
        boolean isLoginRequest = path.equals("/auth") && "POST".equals(method);
        boolean isErrorPage = path.startsWith("/error");
        boolean isSignupRequest = path.startsWith("/users") && "POST".equals(method);

        return isLoginRequest || isErrorPage || isSignupRequest;
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
            String sessionId = SessionCookieUtils.extractSessionId(request);

            // 세션 스토어에 저장돼있는지 검증, 없으면 예외 발생
            var session = sessionManager.requireSession(sessionId);

            // 세션의 userId를 request attribute에 저장 (컨트롤러에서 사용)
            request.setAttribute("userId", session.getUserId());

            // 유효한 세션을 가진 요청임이 확인되었으므로 다음으로 진행
            chain.doFilter(request, response);
        } catch (UnauthenticatedException e) {
            // Filter에서 발생한 예외는 @RestControllerAdvice가 처리하지 못하므로 직접 응답
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
        }
    }

}
