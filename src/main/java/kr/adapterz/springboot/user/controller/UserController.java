package kr.adapterz.springboot.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.utils.JwtAuthUtils;
import kr.adapterz.springboot.user.dto.MyProfileResponse;
import kr.adapterz.springboot.user.dto.NicknameUpdateRequest;
import kr.adapterz.springboot.user.dto.PasswordUpdateRequest;
import kr.adapterz.springboot.user.dto.SignupRequest;
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
    public ResponseEntity<MyProfileResponse> getMyInfo(HttpServletRequest request) {
        Long userId = JwtAuthUtils.extractUserId(request);
        MyProfileResponse response = userService.getUser(userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/nickname")
    public ResponseEntity<MyProfileResponse> updateNickname(
            HttpServletRequest request,
            @RequestBody @Valid NicknameUpdateRequest body
    ) {
        Long userId = JwtAuthUtils.extractUserId(request);
        MyProfileResponse response = userService.updateNickname(userId, body);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            HttpServletRequest request,
            @RequestBody @Valid PasswordUpdateRequest body
    ) {
        Long userId = JwtAuthUtils.extractUserId(request);
        userService.updatePassword(userId, body);
        return ResponseEntity.noContent().build();
    }

    /**
     * 회원탈퇴
     */
    @DeleteMapping
    public ResponseEntity<Void> withdraw(HttpServletRequest request) {
        Long userId = JwtAuthUtils.extractUserId(request);
        userService.withdraw(userId);
        return ResponseEntity.noContent().build();
    }
}
