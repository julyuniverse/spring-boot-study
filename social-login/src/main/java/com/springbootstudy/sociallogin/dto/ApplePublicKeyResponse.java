package com.springbootstudy.sociallogin.dto;

import java.util.List;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record ApplePublicKeyResponse(List<ApplePublicKey> keys) {
}
