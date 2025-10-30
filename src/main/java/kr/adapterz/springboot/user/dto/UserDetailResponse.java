package kr.adapterz.springboot.user.dto;

public record UserDetailResponse(
        Long userId,
        String email,
        String nickname
) {
}
