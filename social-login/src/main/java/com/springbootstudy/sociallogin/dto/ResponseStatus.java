package com.springbootstudy.sociallogin.dto;

import com.springbootstudy.sociallogin.enums.StatusCode;
import lombok.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class ResponseStatus {
    private String code;
    private String message;

    public ResponseStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResponseStatus(StatusCode statusCode) {
        this.code = statusCode.getCode();
        this.message = statusCode.getMessage();
    }
}
