package kr.adapterz.springboot.config;

import kr.adapterz.springboot.auth.interceptor.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정
 * <p>
 * JWT 인증 인터셉터 등록 및 공개 경로 설정
 * </p>
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Value("${cors.enabled:false}")
    private boolean corsEnabled;

    @Value("${cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins;

    /**
     * 인터셉터 등록
     * <p>
     * JWT 인증 인터셉터를 모든 경로에 적용하되,
     * 정적 리소스 및 개발 도구 경로는 제외
     * </p>
     * <p>
     * <strong>주의:</strong> excludePathPatterns는 HTTP 메서드를 구분하지 않음
     * </p>
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/**")  // 모든 경로에 적용
                .excludePathPatterns(
                        // 공개 페이지
                        "/error",
                        "/privacy",
                        "/terms",

                        // 개발 도구 (Swagger)
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }

    /**
     * CORS 설정 (로컬 개발 환경용)
     * <p>
     * 로컬 개발 시 프론트엔드(localhost:3000)에서 백엔드(localhost:8080)로 직접 요청 가능하도록 설정.
     * 배포 환경에서는 ALB가 라우팅하므로 CORS 불필요 (cors.enabled=false)
     * </p>
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (corsEnabled) {
            registry.addMapping("/**")
                    .allowedOrigins(allowedOrigins.split(","))
                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE")
                    .allowCredentials(true)  // Refresh Token 쿠키 전송 허용
                    .maxAge(3600);
        }
    }
}