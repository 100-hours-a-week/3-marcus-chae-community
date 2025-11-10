package kr.adapterz.springboot.auth.dto;

import kr.adapterz.springboot.user.dto.MyProfileResponse;

public record LoginResponse(
        String accessToken,
        MyProfileResponse myProfileResponse
) {
}
