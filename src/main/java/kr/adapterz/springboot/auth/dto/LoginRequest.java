package kr.adapterz.springboot.auth.dto;

import jakarta.validation.constraints.Email;
import kr.adapterz.springboot.common.validation.ValidPassword;

/**
 *
 * @param email
 * @param password
 */
public record LoginRequest(
        @Email
        String email,

        @ValidPassword
        String password
) {
}
