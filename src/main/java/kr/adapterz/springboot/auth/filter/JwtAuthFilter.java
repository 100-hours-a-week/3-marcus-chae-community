package kr.adapterz.springboot.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.adapterz.springboot.auth.exception.UnauthenticatedException;
import kr.adapterz.springboot.auth.jwt.JwtProvider;
import kr.adapterz.springboot.auth.utils.JwtAuthUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * 세션 인증 필터 제외 대상
     * <p>
     * 제외할 경로는 application.yml에 분리해놓으면 좋음.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String method = request.getMethod();

        // 공개 페이지 및 개발 도구
        if (path.startsWith("/error")
                || path.equals("/privacy")
                || path.equals("/terms")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")) {
            return true;
        }

        // API 엔드포인트별 공개 여부 판단
        return switch (method) {
            case "POST" -> path.equals("/auth") || path.startsWith("/users");
            case "GET" -> path.startsWith("/posts");
            default -> false;
        };
    }

    /**
     * JWT 토큰 기반 요청 인증 필터.
     * <p>
     * {@link #shouldNotFilter}에서 스킵되지 못한 모든 요청에 적용되며, 다음 두 가지를 수행합니다:
     * <ul>
     *   <li>JWT 토큰 검증: 정당한 토큰인지 서명 및 만료 여부 확인</li>
     *   <li>사용자 정보 저장: userId를 request attribute에 저장하여 컨트롤러에서 접근 가능하게 함</li>
     * </ul>
     * @param request  요청 객체 (Authorization 헤더의 토큰 추출에 사용)
     * @param response 응답 객체 (인증 실패 시 401 상태코드 반환)
     * @param chain    필터 체인
     *
     * @throws IOException                  입출력 오류 발생 시
     * @throws ServletException             서블릿 처리 오류 발생 시
     * @throws UnauthenticatedException     토큰이 없거나 형식이 잘못된 경우
     * @throws io.jsonwebtoken.JwtException 토큰 검증 실패 (서명 오류, 만료 등)
     * @see JwtAuthUtils#extractTokenFromHeader
     * @see JwtProvider#parseToken
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws IOException, ServletException {

     // todo: 만료된 엑세스토큰이면 리프레시 엔드포인트로 리다이렉트
        try {
            // 요청에서 토큰 추출
            String token = JwtAuthUtils.extractTokenFromHeader(request);

            // jwt 검증
            var jws = jwtProvider.parseToken(token);
            Claims claims = jws.getBody();

            // 클레임에서 검증된 userId 추출
            Long userId = extractUserIdFromClaims(claims);
            request.setAttribute("userId", userId);

            // 유효한 세션을 가진 요청임이 확인되었으므로 다음으로 진행
            chain.doFilter(request, response);

        } catch (
                UnauthenticatedException e) { // Filter에서 발생한 예외는 @RestControllerAdvice가 처리하지 못하므로 전역 예외 핸들러를 통하지 않고 직접 응답. 어떻게 보면 중복 코드이고, 최종 예외처리의 책임이 분산되는 것이기 때문에 살짝 거슬림.
            // 토큰 없음, Authorization 헤더 형식 오류 등
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"인증이 필요합니다.\"}");
        } catch (JwtException e) {
            // JWT 파싱 실패, 서명 오류, 만료 등
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"유효하지 않은 토큰입니다.\"}");
        }
    }

    /**
     * JWT 클레임에서 검증된 사용자 ID를 추출합니다.
     * <p>
     * subject 필드를 확인하고 null인 경우 UnauthenticatedException을 발생시킵니다.
     * subject가 존재하면 Long으로 변환하여 반환합니다.
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
