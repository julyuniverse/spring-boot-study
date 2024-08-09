package com.springbootstudy.sociallogin.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SpringSecurityConfig {
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final TokenProvider tokenProvider;
    private final RedisService redisService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/**").permitAll() // auth 관련 api는 토큰이 없는 상태에서 요청이 들어오기 때문에 permitAll로 설정한다.
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // spring security는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 때문에 세션 설정을 SessionCreationPolicy.STATELESS로 설정
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // TokenFilter 삽입
                // TokenFilter가 UsernamePasswordAuthenticationFilter보다 먼저 실행되도록 설정
                .addFilterBefore(new TokenFilter(tokenProvider, redisService), UsernamePasswordAuthenticationFilter.class)

                // exception handling 할 때 직접 만든 클래스를 적용
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                        .accessDeniedHandler(tokenAccessDeniedHandler)
                );

        return http.build();
    }
}
