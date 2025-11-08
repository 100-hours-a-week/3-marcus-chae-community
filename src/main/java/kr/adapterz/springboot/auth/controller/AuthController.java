package kr.adapterz.springboot.auth.controller;

import jakarta.validation.Valid;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import kr.adapterz.springboot.auth.dto.LoginRequest;
import kr.adapterz.springboot.auth.dto.LoginResponse;
import kr.adapterz.springboot.auth.exception.InvalidCredentialsException;
import kr.adapterz.springboot.auth.jwt.JwtProvider;
import kr.adapterz.springboot.auth.session.SessionManager;
import kr.adapterz.springboot.auth.utils.PasswordUtils;
import kr.adapterz.springboot.user.dto.MyProfileResponse;
import kr.adapterz.springboot.user.entity.User;
import kr.adapterz.springboot.user.exception.UserNotFoundException;
import kr.adapterz.springboot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final SessionManager sessionManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(UserNotFoundException::new);

        if (!PasswordUtils.matches(user, req.password(), passwordEncoder)) {
            throw new InvalidCredentialsException();
        }

        String sessionId = sessionManager.createSession(user.getId()).getSessionId();
        String accessToken = jwtProvider.createAccessToken(user.getId());

        // 여기서 엑세스 로그인 성공한 사용자한테는 엑세스 토큰을 만들어줘야 한다.
        // 만들어주는 것은 jwtprovider.createaccestoken으로. 그거 만들 때 필요한 건 유저아디 뿐.
        // 이제 그다음에는? 그렇게 만든 토큰을 응답에 실어줘야겠지?
        // 단순히 생각하면 auth 헤더에 넣으면 될 거 같은데, 어디선가 쿠키에 넣는다는 얘기도 본 거 같다. 쿠키에 넣어도 되고 헤더에 넣어도 되는 건가? 헤더에 넣으면 로컬스토리지에 저장해놨다가 요청 시 보내는 건가? 세션 스토리지는 안쓰는 게 맞을 거 같고. 탭 닫으면 사라지니까.
        // 둘 다 됨. 쿠키에 넣으면 auth 헤더는 안 쓰는 거임. 근데 우리는 보안성이 중요하지 않은데다가, 어차피 ttl 15분뿐이라 걍 로컬스토리지에 저장하자. 그리고 리프레시 토큰은 httponly 쿠키로 넣어주자고. samesite는 잘 이해 못 했으니 안 쓰기로.

        // 요약하자면, 여기선 바디에 jwt 넣어서 보내기만 하면 됨.
        // 그럼 다음부턴 클라 몫.

        // 바디에 토큰을 넣어서 전달해주려면 dto에 토큰 필드 추가해야함.

        // todo: 리프레시 토큰 set-cookie 응답에 추가하기
        // 임시: 액세스 토큰 테스트 중이므로 가짜 쿠키만 넣기
        String fakeCookie = "refresh_token=temp; Path=/; HttpOnly"; // path?? 이건 뭐지

        return ResponseEntity.status(201)
                .header(HttpHeaders.SET_COOKIE, fakeCookie)
                .body(new LoginResponse(accessToken, MyProfileResponse.from(user))); // todo: 클라가 로컬 스토리지에다가 토큰 저장하고 다음부터 요청할 때 항상 auth헤더에다가 jwt 넣도록 js 수정
    }

    @DeleteMapping
    public ResponseEntity<Void> logout(@CookieValue(AuthConstants.SESSION_COOKIE_NAME) String sessionId) {
        // todo: 브라우저의 리프레시 토큰 쿠키를 제거하는 응답 추가하기.
        sessionManager.expire(sessionId);
        return ResponseEntity.ok().build();
    }
}
