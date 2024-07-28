package com.springbootstudy.sociallogin.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record TokenResponse(String access_token, String token_type, Integer expires_in, String refresh_token, String id_token) {
}
