package kr.adapterz.springboot.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record NicknameUpdateRequest(
        @NotEmpty
        @Size(max = 10)
        String nickname
) {
}
