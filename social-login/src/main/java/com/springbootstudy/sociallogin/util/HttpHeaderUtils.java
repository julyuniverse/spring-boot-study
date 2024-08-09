package com.springbootstudy.sociallogin.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

/**
 * @author Lee Taesung
 * @since 1.0
 */
public class HttpHeaderUtils {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String PLATFORM_HEADER = "Platform";
    public static final String DEVICE_ID_HEADER = "Device-ID";

    /**
     * @return authorization token
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getAuthorizationToken() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

    /**
     * @return platform
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getPlatform() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader(PLATFORM_HEADER);
    }

    /**
     * @return device id
     * @author Lee Taesung
     * @since 1.0
     */
    public static String getDeviceId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader(DEVICE_ID_HEADER);
    }
}
