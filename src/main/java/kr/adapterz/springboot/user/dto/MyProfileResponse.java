package kr.adapterz.springboot.user.dto;

public record MyProfileResponse(
        Long userId,
        String email,
        String nickname
) {
}
