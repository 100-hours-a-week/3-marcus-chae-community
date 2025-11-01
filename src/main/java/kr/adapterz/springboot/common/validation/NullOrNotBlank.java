package kr.adapterz.springboot.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * null이거나 공백이 아닌 문자열만 허용하는 검증 어노테이션
 * - null: 허용 (부분 업데이트 지원)
 * - 빈 문자열(""): 거부
 * - 공백만 있는 문자열("   "): 거부
 * - 유효한 문자열: 허용
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {NullOrNotBlankValidator.class})
public @interface NullOrNotBlank {
    String message() default "값이 제공된 경우 공백일 수 없습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}