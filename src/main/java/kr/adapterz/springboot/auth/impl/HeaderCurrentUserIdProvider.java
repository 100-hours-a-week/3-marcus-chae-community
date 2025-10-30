package kr.adapterz.springboot.auth.impl;

import kr.adapterz.springboot.auth.CurrentUserIdProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

/**
 * @deprecated 임시 구현체였으며 인증 절차 구현됨. 사용 금지.
 */
@Component
@Profile({"dev"})
@Deprecated
public class HeaderCurrentUserIdProvider implements CurrentUserIdProvider {
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