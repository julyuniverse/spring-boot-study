package com.springbootstudy.sociallogin.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record SocialLoginRequest(
        String idToken,
        String authorizationCode,
        String firstName,
        String lastName
) {
}
