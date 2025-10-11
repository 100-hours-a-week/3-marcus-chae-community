package kr.adapterz.springboot.common.auth.impl;

import kr.adapterz.springboot.common.auth.CurrentUserProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * <b>주의: 임시 구현체</b></br>
 * 개발과정에서 JWT 인증 구현 전에 요청자의 id를 요청 메세지에서 얻기 위해 만든 임시 구현체. X-USER-ID 같은 헤더는 비표준이며 권장되지 않기 때문에 추후 꼭 대체되어야함.
 */
@Component
@Profile({"dev"})
public class HeaderCurrentUserProvider implements CurrentUserProvider {
    private static final String HEADER = "X-USER-ID";

    @Override
    public Optional<Long> getId() {
        var attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null) return Optional.empty();
        var raw = attrs.getRequest().getHeader(HEADER);
        if (raw == null || raw.isBlank()) return Optional.empty();
        try {
            return Optional.of(Long.parseLong(raw));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}