package kr.adapterz.springboot.common.auth;

import kr.adapterz.springboot.common.exception.UnauthenticatedException;

import java.util.Optional;

public interface CurrentUserProvider {
    Optional<Long> getId();

    default long requireId() {
        return getId().orElseThrow(UnauthenticatedException::new);
    }

    default boolean isAuthenticated() {
        return getId().isPresent();
    }
}