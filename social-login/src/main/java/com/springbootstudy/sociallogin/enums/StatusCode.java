package com.springbootstudy.sociallogin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@AllArgsConstructor
public enum StatusCode {
    // common
    FAILURE("C0000", "Failure."),
    SUCCESS("C0001", "Success."),
    ACCOUNT_NOT_FOUND("C0002", "Account not found."),
    ;

    private final String code;
    private final String message;
}
