package kr.adapterz.springboot.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        @Email
        String email,

        @NotBlank
        String password,

        @NotBlank
        @Size(max = 10)
        String nickname
) {
}
