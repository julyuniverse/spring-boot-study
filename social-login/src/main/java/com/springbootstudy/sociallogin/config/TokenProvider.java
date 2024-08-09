package com.springbootstudy.sociallogin.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.springbootstudy.sociallogin.enums.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
@Slf4j
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final String TOKEN_TYPE_KEY = "tokenType";
    private static final String DEVICE_ID_KEY = "deviceId";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    @Value("${token.ttl.access-token}")
    private Long accessTokenTtl;
    @Value("${token.ttl.refresh-token}")
    private Long refreshTokenTtl;

    public TokenProvider(@Value("${token.secret}") String secretKey) {
        byte[] keyBytes = Base64.getDecoder().decode(secretKey);
        this.algorithm = Algorithm.HMAC256(keyBytes);
        this.verifier = JWT.require(algorithm).build();
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    private String createToken(Authentication authentication, long ttl, TokenType tokenType, String deviceId) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        long now = (new Date()).getTime();

        return JWT.create()
                .withSubject(authentication.getName())          // payload "sub": "accountId" (ex)
                .withClaim(AUTHORITIES_KEY, authorities)        // payload "auth": "ROLE_USER" (ex)
                .withClaim(TOKEN_TYPE_KEY, tokenType.name())    // payload "tokenType": "ACCESS" (ex)
                .withClaim(DEVICE_ID_KEY, deviceId)             // payload "deviceId": "1" (ex)
                .withIssuedAt(new Date(now))                    // payload "iat": 1516239022 (ex)
                .withExpiresAt(new Date(now + ttl))             // payload "exp": 1516239022 (ex)
                .sign(algorithm);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public String createAccessToken(Authentication authentication, String deviceId) {
        return createToken(authentication, accessTokenTtl, TokenType.ACCESS, deviceId);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public String createRefreshToken(Authentication authentication, String deviceId) {
        return createToken(authentication, refreshTokenTtl, TokenType.REFRESH, deviceId);
    }

    /**
     * 토큰 유효성 검사
     *
     * @param checkExpiration 만료 시간은 검증할지 여부
     * @author Lee Taesung
     * @since 1.0
     */
    public DecodedJWT validateToken(String token, TokenType tokenType, boolean checkExpiration) {
        try {
            DecodedJWT decodedJWT = this.verifier.verify(token);

            // 권한 검사
            String authorities = decodedJWT.getClaim(AUTHORITIES_KEY).asString();
            if (!StringUtils.hasText(authorities)) {
                log.info("[validateToken] 권한 정보가 없는 토큰");
                throw new CustomException(ErrorCode.TOKEN_NO_AUTHORITY);
            }

            // 토큰 타입 검사
            String extractedTokenType = decodedJWT.getClaim(TOKEN_TYPE_KEY).asString();
            if (StringUtils.hasText(extractedTokenType)) {
                if (tokenType == TokenType.ACCESS) {
                    if (!Objects.equals(extractedTokenType, TokenType.ACCESS.name())) {
                        log.info("[validateToken] 액세스 토큰이 아님");
                        throw new CustomException(ErrorCode.NOT_AN_ACCESS_TOKEN);
                    }
                } else if (tokenType == TokenType.REFRESH) {
                    if (!Objects.equals(extractedTokenType, TokenType.REFRESH.name())) {
                        log.info("[validateToken] 리프레쉬 토큰이 아님");
                        throw new CustomException(ErrorCode.NOT_A_REFRESH_TOKEN);
                    }
                } else {
                    log.info("[validateToken] 토큰 타입이 없는 경우");
                    throw new CustomException(ErrorCode.NO_TOKEN_TYPE);
                }
            } else {
                log.info("[validateToken] 토큰 타입이 없는 경우");
                throw new CustomException(ErrorCode.NO_TOKEN_TYPE);
            }

            // 디바이스 아이디 검사
            String deviceId = decodedJWT.getClaim(DEVICE_ID_KEY).asString();
            if (!StringUtils.hasText(deviceId)) {
                log.info("[validateToken] 디바이스 아이디가 없는 경우");
                throw new CustomException(ErrorCode.NO_DEVICE_ID);
            }

            return decodedJWT;
        } catch (TokenExpiredException e) {
            if (checkExpiration) {
                log.info("[validateToken] 만료된 토큰");
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            } else {
                return JWT.decode(token);
            }
        } catch (SignatureVerificationException e) {
            log.info("[validateToken] 잘못된 서명의 토큰");
            throw new CustomException(ErrorCode.INVALID_TOKEN_SIGNATURE);
        } catch (JWTDecodeException e) {
            log.info("[validateToken] 디코딩에 실패한 토큰");
            throw new CustomException(ErrorCode.TOKEN_DECODING_FAILED);
        } catch (JWTVerificationException e) {
            log.info("[validateToken] 검증에 실패한 토큰");
            throw new CustomException(ErrorCode.TOKEN_VERIFICATION_FAILED);
        }
    }

    /**
     * @return 만료를 제외한 토큰의 모든 유효성 검사 후 Authentication 객체로 가공해서 반환
     * @author Lee Taesung
     * @since 1.0
     */
    public Authentication getAuthentication(String token, TokenType tokenType) {
        // 토큰 검증
        DecodedJWT decodedJWT = validateToken(token, tokenType, false);

        // 클래임에서 권한 정보 가져오기.
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(decodedJWT.getClaim(AUTHORITIES_KEY).asString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 객체 반환
        UserDetails principal = new User(decodedJWT.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}
