package com.springbootstudy.sociallogin.dto;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public record SocialLoginResponse(ResponseStatus responseStatus, AccountDto account, TokenDto token) {
}
