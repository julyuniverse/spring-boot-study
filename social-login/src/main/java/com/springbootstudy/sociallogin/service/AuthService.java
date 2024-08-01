package com.springbootstudy.sociallogin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbootstudy.sociallogin.dto.*;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    @Value("${apple.private-key}")
    private String applePrivateKey;
    @Value("${apple.client-id}")
    private String appleClientId;
    @Value("${apple.aud}")
    private String appleAud;
    @Value("${apple.redirect-uri}")
    private String appleRedirectUri;
    @Value("${apple.key.id}")
    private String appleKeyId;
    @Value("${apple.team-id}")
    private String appleTeamId;
    @Value("${apple.iss}")
    private String appleIss;
    private final HttpServletRequest httpServletRequest;

    /**
     * 소셜 제공자를 통해 로그인
     *
     * @author Lee Taesung
     * @since 1.0
     */
    public void loginWithSocialProvider(SocialLoginRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        // 플랫폼 확인
        String platform = httpServletRequest.getHeader("platform");
        if (Objects.equals(platform, "iOS")) { // apple
            WebClient appleClient = WebClient.builder()
                    .baseUrl("https://appleid.apple.com/auth")
                    .build();

            // 1. apple로부터 공개키 3개 가져오기
            ApplePublicKeyResponse applePublicKeyResponse = appleClient
                    .get()
                    .uri("/keys")
                    .retrieve()
                    .bodyToMono(ApplePublicKeyResponse.class)
                    .block();
            System.out.println(applePublicKeyResponse);

            // 2. idToken 검증
            String idTokenHeader = request.idToken().substring(0, request.idToken().indexOf("."));
            String decodedIdTokenHeader = new String(java.util.Base64.getDecoder().decode(idTokenHeader), StandardCharsets.UTF_8);
            Map<String, String> idTokenMap = new ObjectMapper().readValue(decodedIdTokenHeader, Map.class);
            ApplePublicKey applePublicKey = applePublicKeyResponse.keys().stream()
                    .filter(key -> Objects.equals(key.kid(), idTokenMap.get("kid")) && Objects.equals(key.alg(), idTokenMap.get("alg")))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Apple public key not found."));
            byte[] nBytes = java.util.Base64.getUrlDecoder().decode(applePublicKey.n());
            byte[] eBytes = java.util.Base64.getUrlDecoder().decode(applePublicKey.e());
            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(applePublicKey.kty());
            PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(request.idToken())
                    .getPayload();
            System.out.println(claims);

            // 3. apple로부터 토큰 요청
            Mono<AppleTokenResponse> appleTokenResponseMono = appleClient
                    .post()
                    .uri("/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .body(BodyInserters.fromFormData("client_id", appleClientId)
                            .with("client_secret", createClientSecret())
                            .with("code", request.authorizationCode())
                            .with("grant_type", "authorization_code"))
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                        // 로그를 찍거나 예외를 던짐
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.out.println("Error body: " + errorBody); // 로그 출력
                                    return Mono.error(new WebClientResponseException(
                                            clientResponse.statusCode().value(),
                                            clientResponse.statusCode().toString(),
                                            clientResponse.headers().asHttpHeaders(),
                                            errorBody.getBytes(),
                                            null,
                                            null
                                    ));
                                });
                    })
                    .bodyToMono(AppleTokenResponse.class);
            try {
                AppleTokenResponse appleTokenResponse = appleTokenResponseMono.block();
                System.out.println(appleTokenResponse);
            } catch (WebClientResponseException ex) {
                System.out.println("Error status code: " + ex.getStatusCode());
                System.out.println("Error response body: " + ex.getResponseBodyAsString());
            }
        }
    }

    public String createClientSecret() {
        Date issuedAt = new Date();
        Date expiration = new Date(issuedAt.getTime() + 3600 * 24 * 1000);
        try {
            return Jwts.builder()
                    .header()
                    .add("alg", "ES256")
                    .add("kid", appleKeyId)
                    .and()
                    .issuer(appleTeamId)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .audience()
                    .add("https://appleid.apple.com")
                    .and()
                    .subject(appleClientId)
                    .signWith(getPrivateKey(), Jwts.SIG.ES256)
                    .compact();
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Failed to create client secret", e);
        }
    }

    public PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] encoded = Base64.getDecoder().decode(applePrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");

        return keyFactory.generatePrivate(keySpec);
    }

    public void refreshAppleToken(String refreshToken) {
        WebClient appleClient = WebClient.builder()
                .baseUrl("https://appleid.apple.com/auth")
                .build();
        Mono<AppleTokenResponse> appleTokenResponseMono = appleClient
                .post()
                .uri("/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("client_id", appleClientId)
                        .with("client_secret", createClientSecret())
                        .with("refresh_token", refreshToken)
                        .with("grant_type", "refresh_token"))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), clientResponse -> {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                System.out.println("Error body: " + errorBody); // 로그 출력
                                return Mono.error(new WebClientResponseException(
                                        clientResponse.statusCode().value(),
                                        clientResponse.statusCode().toString(),
                                        clientResponse.headers().asHttpHeaders(),
                                        errorBody.getBytes(),
                                        null,
                                        null
                                ));
                            });
                })
                .bodyToMono(AppleTokenResponse.class);

        try {
            AppleTokenResponse appleTokenResponse = appleTokenResponseMono.block();
            System.out.println(appleTokenResponse);
        } catch (WebClientResponseException ex) {
            System.out.println("Error status code: " + ex.getStatusCode());
            System.out.println("Error response body: " + ex.getResponseBodyAsString());
            throw new RuntimeException("Failed to refresh token", ex);
        }
    }
}
