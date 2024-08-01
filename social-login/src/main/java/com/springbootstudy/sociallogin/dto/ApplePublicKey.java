package com.springbootstudy.sociallogin.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record ApplePublicKey(
        String kty,
        String kid,
        String use,
        String alg,
        String n,
        String e
) {
}
