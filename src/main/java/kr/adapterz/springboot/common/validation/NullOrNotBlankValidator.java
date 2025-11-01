package kr.adapterz.springboot.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * NullOrNotBlank 어노테이션의 검증 로직 구현
 */
public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null은 허용 (부분 업데이트 시 해당 필드를 수정하지 않음)
        if (value == null) {
            return true;
        }

        // 값이 있으면 공백이 아니어야 함
        return !value.isBlank();
    }
}