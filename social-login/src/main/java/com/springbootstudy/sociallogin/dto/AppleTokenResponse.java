package com.springbootstudy.sociallogin.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AppleTokenResponse(
        @JsonProperty(value = "access_token") String accessToken,
        @JsonProperty(value = "token_type") String tokenType,
        @JsonProperty(value = "expires_in") String expiresIn,
        @JsonProperty(value = "refresh_token") String refreshToken,
        @JsonProperty(value = "id_token") String idToken,
        String error
) {
}
