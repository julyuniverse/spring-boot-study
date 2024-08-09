package com.springbootstudy.sociallogin.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record UuidLoginRequest(String deviceUuid, String deviceModel, String systemName, String systemVersion) {
}
