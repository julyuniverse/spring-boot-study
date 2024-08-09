package com.springbootstudy.sociallogin.config;

import com.springbootstudy.sociallogin.enums.TokenType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.springbootstudy.sociallogin.util.HttpHeaderUtils.getAuthorizationToken;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@RequiredArgsConstructor
@Slf4j
public class TokenFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    /**
     * 실제 필터링 로직은 doFilterInternal 메서드에 들어감.
     * 토큰의 인증 정보를 현재 쓰레드의 SecurityContext 객체에 저장하는 역할 수행
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        log.info("[doFilterInternal] proceed");
        // 1. request header에서 토큰을 꺼냄.
        String token = getAuthorizationToken();
        try {
            // 2. 토큰가 null 또는 빈값이 아닌지 검사
            if (StringUtils.hasText(token)) {
                // 3. validateToken으로 토큰 유효성 검사
                tokenProvider.validateToken(token, TokenType.ACCESS, true);

                // 4. redis 블랙 리스트에서 logout된 access token이 있는지 확인
                String loggedOutToken = redisService.getData(token);
                if (!ObjectUtils.isEmpty(loggedOutToken)) { // logout된 access token이 존재한다면
                    log.info("[doFilterInternal] 로그아웃된 계정");
                    request.setAttribute("Token-Exception", ErrorCode.ACCOUNT_LOGGED_OUT.name());
                } else {
                    // 클레임 정보 검사 (권한, 토큰 타입 등등..)
                    // 정상 토큰이면 해당 토큰으로 Authentication 객체을 가져와서 SecurityContext 객체에 저장
                    Authentication authentication = tokenProvider.getAuthentication(token, TokenType.ACCESS);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } else {
                log.info("[doFilterInternal] Authorization header에 토큰이 제공되지 않음");
                request.setAttribute("Token-Exception", ErrorCode.NO_TOKEN_PROVIDED.name());
            }
        } catch (CustomException e) {
            log.info("[doFilterInternal] ErrorCode: {}", e.getErrorCode());
            request.setAttribute("Token-Exception", e.getErrorCode().name());
        } catch (Exception e) {
            request.setAttribute("Token-Exception", ErrorCode.FAILED.name());
            log.error("[doFilterInternal] ================================================");
            log.error("[doFilterInternal] doFilterInternal() error occurred");
            log.error("[doFilterInternal] token: {}", token);
            log.error("[doFilterInternal] Exception Message: {}", e.getMessage());
            log.error("[doFilterInternal] Exception StackTrace: {", e);
            log.error("}");
            log.error("[doFilterInternal] ================================================");
        }
        filterChain.doFilter(request, response);
    }
}
