package kr.adapterz.springboot.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.CurrentUserIdProvider;
import kr.adapterz.springboot.user.dto.SignupRequest;
import kr.adapterz.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserIdProvider currentUserIdProvider;

    /**
     * 일단 필터링 테스트 용도로만 간단히 작성. 후에 실제 의미 있는 플로우를 실행하도록 재작성 필요.
     *
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<String> getMyInfo(HttpServletRequest request) {



        return ResponseEntity.ok("요청이 검증 성공하여 필터 무사히 통과됨. 즉, 회원정보 조회 요청이 컨트롤러에 성공적으로 전달됨.");
    }

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest req) {
        userService.signup(req);
        return ResponseEntity.status(201).build(); // 서비스에서 예외를 던지지 않았다면 성공적으로 등록된 것이므로 201 응답
    }
}
