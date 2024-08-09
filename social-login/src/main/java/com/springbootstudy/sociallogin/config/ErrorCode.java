package com.springbootstudy.sociallogin.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@AllArgsConstructor
@Getter
public enum ErrorCode {
    FAILED(BAD_REQUEST, "Failed."),
    EXPIRED_TOKEN(UNAUTHORIZED, "The token has expired."),
    INVALID_TOKEN_SIGNATURE(UNAUTHORIZED, "The token signature is invalid."),
    TOKEN_DECODING_FAILED(UNAUTHORIZED, "Failed to decode the token."),
    TOKEN_VERIFICATION_FAILED(UNAUTHORIZED, "Token verification failed."),
    TOKEN_NO_AUTHORITY(UNAUTHORIZED, "The token has no authority information."),
    NOT_AN_ACCESS_TOKEN(UNAUTHORIZED, "The token is not an access token."),
    NOT_A_REFRESH_TOKEN(UNAUTHORIZED, "The token is not a refresh token."),
    NO_TOKEN_TYPE(UNAUTHORIZED, "The token type is missing."),
    NO_DEVICE_ID(UNAUTHORIZED, "The device ID is missing."),
    ACCOUNT_LOGGED_OUT(UNAUTHORIZED, "The account has been logged out."),
    NO_TOKEN_PROVIDED(UNAUTHORIZED, "No token provided."),
    TOKEN_MISMATCH(UNAUTHORIZED, "The token does not match."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
