package com.springbootstudy.sociallogin.dto;

import lombok.*;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseStatus {
    private String code;
    private String message;
}
