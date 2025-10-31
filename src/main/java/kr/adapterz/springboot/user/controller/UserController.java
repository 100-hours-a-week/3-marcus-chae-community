package kr.adapterz.springboot.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.utils.SessionCookieUtils;
import kr.adapterz.springboot.user.dto.EditNicknameRequest;
import kr.adapterz.springboot.user.dto.EditPasswordRequest;
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

    @PostMapping
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        userService.signup(request);
        return ResponseEntity.status(HttpServletResponse.SC_CREATED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailResponse> getMyInfo(HttpServletRequest request) {
        Long userId = SessionCookieUtils.extractUserId(request);
        UserDetailResponse response = userService.getUserDetail(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<UserDetailResponse> editNickname(
            HttpServletRequest request,
            @RequestBody @Valid EditNicknameRequest body
    ) {
        Long userId = SessionCookieUtils.extractUserId(request);
        UserDetailResponse response = userService.editNickname(userId, body);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> editPassword(
            HttpServletRequest request,
            @RequestBody @Valid EditPasswordRequest body
    ) {
        Long userId = SessionCookieUtils.extractUserId(request);
        userService.editPassword(userId, body);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping
    public ResponseEntity<Void> withdraw(HttpServletRequest request) {
        Long userId = SessionCookieUtils.extractUserId(request);
        userService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
