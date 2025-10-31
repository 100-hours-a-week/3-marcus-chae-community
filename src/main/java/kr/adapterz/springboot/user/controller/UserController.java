package kr.adapterz.springboot.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.filter.SessionAuthFilter;
import kr.adapterz.springboot.auth.session.Session;
import kr.adapterz.springboot.auth.session.SessionManager;
import kr.adapterz.springboot.user.dto.SignupRequest;
import kr.adapterz.springboot.user.dto.UserDetailResponse;
import kr.adapterz.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SessionManager sessionManager;

    /**
     * 회원정보 조회
     * @param request
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> getMyInfo(HttpServletRequest request) {
        // HttpServletRequest로부터 세션 ID 추출
        String sessionId = SessionAuthFilter.extractSessionId(request);
        // 세션매니저로부터 세션 객체 획득
        Session session = sessionManager.requireSession(sessionId);
        UserDetailResponse response = userService.getUserDetail(session.getUserId());

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest req) {
        userService.signup(req);
        return ResponseEntity.status(201).build();
    }
}
