package kr.adapterz.springboot.user.dto;

import kr.adapterz.springboot.common.validation.ValidPassword;

public record EditPasswordRequest(
        @ValidPassword
        String originalPassword,
        @ValidPassword
        String newPassword
) {
}
