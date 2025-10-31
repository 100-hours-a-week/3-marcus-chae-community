package kr.adapterz.springboot.auth.utils;

import kr.adapterz.springboot.user.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 검증 유틸리티
 */
public final class PasswordUtils {

    private PasswordUtils() {
        throw new AssertionError("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * 사용자의 비밀번호가 일치하는지 검증
     *
     * @param user 사용자 엔티티
     * @param rawPassword 평문 비밀번호
     * @param passwordEncoder 비밀번호 인코더
     * @return 비밀번호 일치 여부
     */
    public static boolean matches(User user, String rawPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}