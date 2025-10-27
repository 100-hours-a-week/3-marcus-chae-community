package kr.adapterz.springboot.config;

//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // 개발 중에만 비활성화
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/**").permitAll() // JWT 구현 후 꼭 지울 것
//                        .requestMatchers("/swagger-ui.html", "/v3/api-docs/**", "/auth/**").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
//                        .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
//                        .anyRequest().authenticated()
//                );
//        return http.build();
//    }
//}
