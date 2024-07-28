package com.springbootstudy.sociallogin.service;

import com.springbootstudy.sociallogin.dto.SocialLoginRequest;
import com.springbootstudy.sociallogin.dto.TokenResponse;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
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
//        // 1. 플랫폼 확인
//        String platform = httpServletRequest.getHeader("platform");
//        if (Objects.equals(platform, "iOS")) { // apple
//            // 2. 토큰 인증
//            Date issuedAt = new Date();
//            Date expiration = new Date(issuedAt.getTime() + 3600 * 24 * 1000);
//            String jwt = Jwts.builder()
//                    .header()
//                    .add("alg", "ES256")
//                    .add("kid", appleKeyId)
//                    .and()
//                    .subject(appleAud)
//                    .issuer(appleTeamId)
//                    .audience()
//                    .add(appleIss)
//                    .and()
//                    .issuedAt(issuedAt)
//                    .expiration(expiration)
//                    .signWith(getPrivateKey(), Jwts.SIG.ES256)
//                    .compact();
//            WebClient webClient = WebClient.builder()
//                    .baseUrl("https://appleid.apple.com/auth/token")
//                    .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
//                    .build();
//            Map<String, Object> requestBody = new HashMap<>();
//            requestBody.put("client_id", appleAud);
//            requestBody.put("client_secret", jwt);
//            requestBody.put("grant_type", "authorization_code");
//            requestBody.put("code", "code");
//            requestBody.put("redirect_uri", appleRedirectUri);
//            TokenResponse tokenResponse = webClient
//                    .post()
//                    .body(BodyInserters.fromValue(requestBody))
//                    .retrieve()
//                    .bodyToMono(TokenResponse.class)
//                    .block();
//            System.out.println(tokenResponse);
//        }
    }

//    private static PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
//        InputStream privateKey = new ClassPathResource("apple").getInputStream();
//        String result = new BufferedReader(new InputStreamReader(privateKey)) .lines().collect(Collectors.joining("\n"));
//        String key = result.replace("-----BEGIN PRIVATE KEY-----\n", "")
//                .replace("-----END PRIVATE KEY-----", "");
//        byte[] encoded = Base64.decodeBase64(key);
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
//        KeyFactory keyFactory = KeyFactory.getInstance("EC");
//
//        return keyFactory.generatePrivate(keySpec);
//    }
}
