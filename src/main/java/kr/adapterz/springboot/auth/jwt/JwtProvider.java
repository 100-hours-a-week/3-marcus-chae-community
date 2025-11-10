package kr.adapterz.springboot.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import kr.adapterz.springboot.auth.constants.AuthConstants;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 생성 및 검증을 담당하는 Provider
 * <ul>
 *   <li>Access Token: 15분 유효</li>
 *   <li>Refresh Token: 60분 유효</li>
 *   <li>알고리즘: HMAC-SHA256</li>
 * </ul>
 */
@Component
public class JwtProvider {

    /**
     * JWT 서명용 비밀키 (HMAC-SHA256)
     * 프로덕션에서는 환경변수로 주입 권장
     */
    private final Key key = Keys.hmacShaKeyFor(
            "adapterzadapterzadapterzadapterzadapterz".getBytes()
    );

    /**
     * Access Token 생성 (유효기간: 15분)
     * Subject에 userId를 저장
     *
     * @param userId 사용자 ID
     * @return JWT 토큰 문자열
     */
    public String createAccessToken(Long userId) {
        long accessTtlSec = AuthConstants.ACCESS_TOKEN_MAX_AGE;
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(accessTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 파싱 및 검증 (서명, 만료시간 체크)
     *
     * @param jwtString JWT 토큰 문자열
     * @return 파싱된 Claims (userId 등 포함)
     * @throws io.jsonwebtoken.ExpiredJwtException 토큰이 만료된 경우
     */
    public Jws<Claims> parseToken(String jwtString) throws ExpiredJwtException {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtString);
    }

    /**
     * Refresh Token 생성 (유효기간: 60분)
     * Access Token 재발급용 장기 토큰
     *
     * @param userId 사용자 ID
     * @return JWT 토큰 문자열 (typ=refresh, jti=UUID)
     */
    public String createRefreshToken(Long userId) {
        long refreshTtlSec = AuthConstants.REFRESH_TOKEN_MAX_AGE;
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("typ", "refresh")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().plusSeconds(refreshTtlSec)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}