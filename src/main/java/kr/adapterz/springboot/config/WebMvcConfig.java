package kr.adapterz.springboot.config;

import kr.adapterz.springboot.auth.interceptor.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
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

    /**
     * 인터셉터 등록
     * <p>
     * JWT 인증 인터셉터를 모든 경로에 적용하되,
     * 정적 리소스 및 개발 도구 경로는 제외
     * </p>
     * <p>
     * <strong>주의:</strong> excludePathPatterns는 HTTP 메서드를 구분하지 않음
     * 메서드별 공개 경로 처리는 {@link JwtAuthInterceptor#isPublicPath} 내부에서 수행
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
}