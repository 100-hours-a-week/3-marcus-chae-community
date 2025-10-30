package kr.adapterz.springboot.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.dto.LoginRequest;
import kr.adapterz.springboot.auth.filter.SessionAuthFilter;
import kr.adapterz.springboot.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity login(@RequestBody @Valid LoginRequest req, HttpServletRequest httpServletRequest) {

        String sessionId = SessionAuthFilter.extractSessionId(httpServletRequest);

        ResponseCookie cookie = ResponseCookie.from(AuthConstants.SESSION_COOKIE_NAME, sessionId)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }

    @DeleteMapping
    public ResponseEntity logout(@CookieValue(AuthConstants.SESSION_COOKIE_NAME) String sessionId) {

        authService.logout(sessionId);

        return ResponseEntity.ok().build();
    }
}
