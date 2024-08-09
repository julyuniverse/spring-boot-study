package com.springbootstudy.sociallogin.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException {
        // 필요한 권한 없이 접근하려 할 때 403 error
        httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
