package kr.adapterz.springboot.user.dto;

import kr.adapterz.springboot.common.validation.ValidPassword;

public record PasswordUpdateRequest(
        @ValidPassword
        String originalPassword,
        @ValidPassword
        String newPassword
) {
}
