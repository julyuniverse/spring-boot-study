package com.springbootstudy.sociallogin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final FilterErrorResponse filterErrorResponse;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        String tokenException = (String) request.getAttribute("Token-Exception");
        if (StringUtils.hasText(tokenException)) {
            if (tokenException.equals(ErrorCode.EXPIRED_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.EXPIRED_TOKEN);
            } else if (tokenException.equals(ErrorCode.INVALID_TOKEN_SIGNATURE.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.INVALID_TOKEN_SIGNATURE);
            } else if (tokenException.equals(ErrorCode.TOKEN_DECODING_FAILED.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.TOKEN_DECODING_FAILED);
            } else if (tokenException.equals(ErrorCode.TOKEN_VERIFICATION_FAILED.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.TOKEN_VERIFICATION_FAILED);
            } else if (tokenException.equals(ErrorCode.TOKEN_NO_AUTHORITY.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.TOKEN_NO_AUTHORITY);
            } else if (tokenException.equals(ErrorCode.NOT_AN_ACCESS_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NOT_AN_ACCESS_TOKEN);
            } else if (tokenException.equals(ErrorCode.NOT_A_REFRESH_TOKEN.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NOT_A_REFRESH_TOKEN);
            } else if (tokenException.equals(ErrorCode.NO_TOKEN_TYPE.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NO_TOKEN_TYPE);
            } else if (tokenException.equals(ErrorCode.NO_DEVICE_ID.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NO_DEVICE_ID);
            } else if (tokenException.equals(ErrorCode.ACCOUNT_LOGGED_OUT.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.ACCOUNT_LOGGED_OUT);
            } else if (tokenException.equals(ErrorCode.NO_TOKEN_PROVIDED.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.NO_TOKEN_PROVIDED);
            } else if (tokenException.equals(ErrorCode.TOKEN_MISMATCH.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.TOKEN_MISMATCH);
            } else if (tokenException.equals(ErrorCode.FAILED.name())) {
                filterErrorResponse.setResponse(response, ErrorCode.FAILED);
            }
        } else {
            log.info("[commence] http header exception에 값이 없어요.");
            filterErrorResponse.setResponse(response, ErrorCode.FAILED);
        }
    }
}
