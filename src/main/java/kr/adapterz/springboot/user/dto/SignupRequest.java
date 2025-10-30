package kr.adapterz.springboot.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.adapterz.springboot.common.validation.ValidPassword;

public record SignupRequest(
        @NotBlank(message = "이메일은 필수입니다")
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,

        @ValidPassword
        String password,

        @NotBlank(message = "닉네임은 필수입니다")
        @Size(max = 10, message = "닉네임은 10자 이하여야 합니다")
        String nickname
) {
}