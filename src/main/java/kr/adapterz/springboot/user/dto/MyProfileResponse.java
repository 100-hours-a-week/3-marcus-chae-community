package kr.adapterz.springboot.user.dto;

import kr.adapterz.springboot.user.entity.User;

import java.time.LocalDateTime;

public record MyProfileResponse(
        Long userId,
        String email,
        String nickname,
        LocalDateTime createdAt
) {
    public static MyProfileResponse from(User user) {
        return new MyProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getCreatedAt()
        );
    }
}
