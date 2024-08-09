package com.springbootstudy.sociallogin.controller;

import com.auth0.jwk.JwkException;
import com.springbootstudy.sociallogin.dto.*;
import com.springbootstudy.sociallogin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @PostMapping("/login/uuid")
    public ResponseEntity<UuidLoginResponse> loginWithUuid(@RequestBody UuidLoginRequest request) {
        return ResponseEntity.ok(authService.loginWithUuid(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @PostMapping("/login/social")
    public ResponseEntity<SocialLoginResponse> loginWithSocialProvider(@RequestBody SocialLoginRequest request) throws IOException, GeneralSecurityException, JwkException {
        return ResponseEntity.ok(authService.loginWithSocialProvider(request));
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    @PostMapping("/token/reissue")
    public ResponseEntity<TokenDto> reissueToken(@RequestBody ReissueTokenRequest request) {
        return ResponseEntity.ok(authService.reissueToken(request));
    }
}
