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
public class UuidLoginResponse {
    private ResponseStatus responseStatus;
    private Integer deviceId;
    private AccountDto account;
}
