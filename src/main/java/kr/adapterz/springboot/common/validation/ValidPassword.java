package kr.adapterz.springboot.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.*;

/**
 * 비밀번호 유효성 검증 커스텀 어노테이션
 */
@NotBlank(message = "비밀번호는 필수입니다")
@Pattern(
        regexp = "^(?!\\s)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}(?<!\\s)$",
        message = "비밀번호는 8-20자, 대소문자/숫자/특수문자 각 1개 이상 포함해야 합니다"
)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {})
public @interface ValidPassword {
    String message() default "유효하지 않은 비밀번호입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
