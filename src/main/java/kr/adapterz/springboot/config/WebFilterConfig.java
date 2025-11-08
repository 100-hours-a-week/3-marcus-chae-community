package kr.adapterz.springboot.config;

import jakarta.servlet.Filter;
import kr.adapterz.springboot.auth.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WebFilterConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * 커스텀 필터를 서블릿 컨테이너에 등록
     */
    @Bean
    public FilterRegistrationBean<Filter> sessionFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(jwtAuthFilter);
        filterRegistrationBean.addUrlPatterns("/*"); // 모든 요청 경로에 필터 적용
        filterRegistrationBean.setOrder(1); // 필터 실행 순위 1. 즉 최우선 실행
        return filterRegistrationBean;
    }
}
