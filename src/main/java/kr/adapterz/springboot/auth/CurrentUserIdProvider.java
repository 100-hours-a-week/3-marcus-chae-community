package kr.adapterz.springboot.auth;

import kr.adapterz.springboot.auth.exception.UnauthenticatedException;

import java.util.Optional;

public interface CurrentUserIdProvider {

    Optional<Long> getId();

    default Long requireId() {
        return getId().orElseThrow(UnauthenticatedException::new);
    }
}