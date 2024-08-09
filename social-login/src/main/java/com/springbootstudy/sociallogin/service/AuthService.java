package com.springbootstudy.sociallogin.service;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkException;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.springbootstudy.sociallogin.config.CustomException;
import com.springbootstudy.sociallogin.config.ErrorCode;
import com.springbootstudy.sociallogin.config.RedisService;
import com.springbootstudy.sociallogin.config.TokenProvider;
import com.springbootstudy.sociallogin.dto.*;
import com.springbootstudy.sociallogin.entity.Account;
import com.springbootstudy.sociallogin.entity.Device;
import com.springbootstudy.sociallogin.enums.Authority;
import com.springbootstudy.sociallogin.enums.StatusCode;
import com.springbootstudy.sociallogin.enums.TokenType;
import com.springbootstudy.sociallogin.repository.AccountRepository;
import com.springbootstudy.sociallogin.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URL;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.util.*;

import static com.springbootstudy.sociallogin.util.ConvertUtils.convertAccountDto;
import static com.springbootstudy.sociallogin.util.HttpHeaderUtils.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private static final String DEFAULT_PASSWORD = "1234";
    @Value("${apple.client-id}")
    private String appleClientId;
    @Value("${google.client-id}")
    private String googleClientId;
    @Value("${token.ttl.refresh-token}")
    private Long refreshTokenTtl;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AccountRepository accountRepository;
    private final DeviceRepository deviceRepository;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    /**
     * uuid를 통해 로그인
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public UuidLoginResponse loginWithUuid(UuidLoginRequest request) {
        log.info("[loginWithUuid] proceed");
        UuidLoginResponse uuidLoginResponse = new UuidLoginResponse();

        // uuid로 기기가 있는지 확인하고 없다면 생성
        Device device = deviceRepository.findByUuid(request.deviceUuid()).orElseGet(() -> Device.builder().uuid(request.deviceUuid()).build());
        device.updateModel(request.deviceModel());
        device.updateSystemName(request.systemName());
        device.updateSystemVersion(request.systemVersion());

        // 디바이스 검사
        if (device.getAccountId() != null) { // accountId가 등록되어 있다면
            Optional<Account> optionalAccount = accountRepository.findByAccountIdAndIsActive(device.getAccountId(), true);
            if (optionalAccount.isPresent()) { // 활성화된 계정이 있다면
                // 계정
                Account account = optionalAccount.get();

                // 반환값 세팅
                uuidLoginResponse.setResponseStatus(new ResponseStatus(StatusCode.SUCCESS));
                uuidLoginResponse.setAccount(convertAccountDto(account));
            } else {
                device.updateAccountId(null);
            }
        } else {
            uuidLoginResponse.setResponseStatus(new ResponseStatus(StatusCode.ACCOUNT_NOT_FOUND));
        }
        deviceRepository.save(device);
        uuidLoginResponse.setDeviceId(device.getDeviceId());

        return uuidLoginResponse;
    }

    /**
     * 소셜 제공자를 통해 로그인
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public SocialLoginResponse loginWithSocialProvider(SocialLoginRequest request) throws IOException, GeneralSecurityException, JwkException {
        log.info("[loginWithSocialProvider] proceed");
        // http header 확인
        String platform = getPlatform();
        Integer deviceId = Integer.parseInt(getDeviceId());
        Account account = null;
        if (Objects.equals(platform, "iOS")) { // apple
            // idToken 검증 5가지
            // apple 개발자 문서: https://developer.apple.com/documentation/sign_in_with_apple/sign_in_with_apple_rest_api/verifying_a_user#3383769
            // Verify the identity token ↓
            // 1. Verify the JWS E256 signature using the server’s public key
            // 2. Verify the nonce for the authentication ↓
            // nonce는 클라이언트에서 생성한 값으로, 보안 상의 이유로 재전송 공격을 방지한다.
            // 클라이언트에서 생성한 nonce를 identityToken을 검증할 때 포함시켜야 하지만, 클라이언트에서 identityToken을 얻기 때문에 서버에서는 이 값을 검증할 수 없다.
            // 보통 이 검증은 클라이언트에서 이루어진다.
            // 3. Verify that the iss field contains https://appleid.apple.com
            // 4. Verify that the aud field is the developer’s client_id
            // 5. Verify that the time is earlier than the exp value of the token

            // 1. server’s public key를 사용한 검증(1번 검증)
            // apple 서버로부터 공개키 3개 가져오기
            JwkProvider provider = new UrlJwkProvider(new URL("https://appleid.apple.com/auth/keys"));
            DecodedJWT jwt = JWT.decode(request.idToken());
            Jwk jwk = provider.get(jwt.getKeyId());

            // 2. 토큰 검증
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(request.idToken());

            // 3. audience 검증
            if (!decodedJWT.getAudience().contains(appleClientId)) {
                throw new RuntimeException("Invalid audience.");
            }

            // 4. expiration 검증
            if (decodedJWT.getExpiresAt().before(new Date())) {
                throw new RuntimeException("Token has expired.");
            }

            // 사용자 정보 추출
            String userIdentifier = decodedJWT.getSubject();
            System.out.println("userIdentifier: " + userIdentifier);
            String email = decodedJWT.getClaim("email").asString();
            System.out.println("email: " + decodedJWT.getClaim("email").asString());
            String firstName = request.firstName();
            String lastName = request.lastName();

            // data insertion
            Optional<Account> optionalAccount = accountRepository.findByUserIdentifier(userIdentifier);
            if (optionalAccount.isPresent()) {
                account = optionalAccount.get();
                account.updateEmail(email);
                if (StringUtils.hasText(firstName)) {
                    account.updateFirstName(firstName);
                }
                if (StringUtils.hasText(lastName)) {
                    account.updateLastName(lastName);
                }
            } else {
                account = Account.builder()
                        .userIdentifier(userIdentifier)
                        .email(email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .authority(Authority.ROLE_USER.name())
                        .password(passwordEncoder.encode(DEFAULT_PASSWORD))
                        .build();
                accountRepository.save(account);
            }
        } else if (Objects.equals(platform, "Android")) { // google
            HttpTransport transport = new NetHttpTransport();
            GsonFactory gsonFactory = new GsonFactory();
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, gsonFactory)
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            GoogleIdToken idToken = verifier.verify(request.idToken());
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String userId = payload.getSubject();
                System.out.println("userId: " + userId);

                // Get profile information from payload
                String email = payload.getEmail();
                Boolean emailVerified = payload.getEmailVerified();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                String locale = (String) payload.get("locale");
                String familyName = (String) payload.get("family_name");
                String givenName = (String) payload.get("given_name");
            } else {
                System.out.println("Invalid idToken.");
            }
        }

        // 기기
        Device device = deviceRepository.findByDeviceId(deviceId);
        device.updateAccountId(account.getAccountId());

        // 토큰 발급
        // access token, refresh token 생성
        // 1. accountId, password 기반 UsernamePasswordAuthenticationToken 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(Integer.toString(account.getAccountId()), DEFAULT_PASSWORD);

        // 2. 사용자 검증
        // authenticationManagerBuilder.getObject().authenticate 메서드가 실행될 때 CustomUserDetailsService에서 만들었던 loadUserByUsername 메서드가 실행됨 -> 사전에 위에서 설정한 UsernamePasswordAuthenticationToken가 반드시 적용되어 있어야 한다.
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 토큰 생성
        TokenDto tokenDto = new TokenDto(tokenProvider.createAccessToken(authentication, String.valueOf(deviceId)), tokenProvider.createRefreshToken(authentication, String.valueOf(deviceId)));

        // 4. redis에 refresh token 생성 (생성 시 키 이름은 어카운트아이디:디바이스아이디로 설정한다.)
        redisService.setData(account.getAccountId() + ":" + device.getDeviceId(), tokenDto.refreshToken(), refreshTokenTtl);

        // 5. 반환값
        SocialLoginResponse socialLoginResponse = new SocialLoginResponse(
                new ResponseStatus(StatusCode.SUCCESS),
                convertAccountDto(account),
                tokenDto
        );
        System.out.println(socialLoginResponse);

        return socialLoginResponse;
    }

    /**
     * 토큰 예외 발생 시 해당 device에 등록된 accountId를 null 처리 및 강제 로그아웃 로그 생성
     *
     * @param deviceId device->deviceId
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional
    public void setNullToDevice(Integer deviceId) {
        Device device = deviceRepository.findByDeviceId(deviceId);
        device.updateAccountId(null);
    }

    /**
     * 토큰 재발행
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Transactional(noRollbackFor = {CustomException.class})
    public TokenDto reissueToken(ReissueTokenRequest request) {
        log.info("[reissueToken] proceed");
        Integer deviceId = Integer.parseInt(getDeviceId());

        // 1. 토큰 검증
        Authentication authentication;
        try {
            // 2. refresh token 검증
            DecodedJWT decodedJWT = tokenProvider.validateToken(request.refreshToken(), TokenType.REFRESH, true);

            // 3. access token에서 어카운트아이디 가져오기
            authentication = tokenProvider.getAuthentication(getAuthorizationToken(), TokenType.ACCESS);

            // 4. access token과 refresh token의 sub가 동일한지 확인
            if (!Objects.equals(decodedJWT.getSubject(), authentication.getName())) {
                throw new CustomException(ErrorCode.TOKEN_MISMATCH);
            }
        } catch (CustomException e) {
            setNullToDevice(deviceId);
            log.info("[reissueToken] ErrorCode: {}", e.getErrorCode().name());
            throw new CustomException(e.getErrorCode());
        }

        // 4. redis에서 (어카운트아이디:디바이스아이디) 기반으로 생성된 refresh token 값 가져오기
        String refreshToken = redisService.getData(authentication.getName() + ":" + deviceId);

        // 5. refresh token 존재 여부 체크
        if (!StringUtils.hasText(refreshToken)) {
            setNullToDevice(deviceId);
            log.info("[reissueToken] 로그아웃된 계정");
            throw new CustomException(ErrorCode.ACCOUNT_LOGGED_OUT);
        }

        // 6. refresh token 매칭 체크
        if (!Objects.equals(refreshToken, request.refreshToken())) {
            setNullToDevice(deviceId);
            log.info("[reissueToken] 일치하지 않는 토큰");
            throw new CustomException(ErrorCode.TOKEN_MISMATCH);
        }

        // 7. 새로운 토큰 생성
        TokenDto tokenDto = new TokenDto(tokenProvider.createAccessToken(authentication, String.valueOf(deviceId)), tokenProvider.createRefreshToken(authentication, String.valueOf(deviceId)));

        // 8. redis에 refresh token 업데이트
        redisService.setData(authentication.getName() + ":" + deviceId, tokenDto.refreshToken(), refreshTokenTtl);

        return tokenDto;
    }
}
