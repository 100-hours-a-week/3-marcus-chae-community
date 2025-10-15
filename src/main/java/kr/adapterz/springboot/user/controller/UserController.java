package kr.adapterz.springboot.user.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.user.dto.SignupRequest;
import kr.adapterz.springboot.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest req) {
        userService.signup(req);
        return ResponseEntity.status(201).build(); // 서비스에서 예외를 던지지 않았다면 성공적으로 등록된 것이므로 201 응답
    }
}
