package kr.adapterz.springboot.auth.constants;

/**
 * 인증 관련 상수
 */
public final class AuthConstants {
    public static final String SESSION_COOKIE_NAME = "sessionId";

    // JWT 만료시간 (초 단위)
    public static final int ACCESS_TOKEN_MAX_AGE = 15 * 60; // 15분
    public static final int REFRESH_TOKEN_MAX_AGE = 60 * 60; // 60분

    // Refresh Token 쿠키 설정
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final String REFRESH_TOKEN_COOKIE_PATH = "/api/v1/auth/refresh";

    // Request Attribute 키
    public static final String USER_ID_ATTRIBUTE = "userId";
}
