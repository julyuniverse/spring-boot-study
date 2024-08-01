package com.springbootstudy.sociallogin.controller;

import com.springbootstudy.sociallogin.dto.ReissueRequest;
import com.springbootstudy.sociallogin.dto.SocialLoginRequest;
import com.springbootstudy.sociallogin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
    @PostMapping("/social-login")
    public ResponseEntity<Void> loginWithSocialProvider(@RequestBody SocialLoginRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        authService.loginWithSocialProvider(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissue(@RequestBody ReissueRequest request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        authService.refreshAppleToken(request.refreshToken());

        return ResponseEntity.ok().build();
    }
}
