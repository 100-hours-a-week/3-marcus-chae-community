package kr.adapterz.springboot.auth.dto;

public record RefreshResponse(
        // 새 엑세스 토큰
        String accessToken
) {
}
