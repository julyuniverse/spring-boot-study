package com.springbootstudy.sociallogin.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisInitializer {
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * db를 초기화하고 데이터를 삭제
     *
     * @author Lee Taesung
     * @since 1.0
     */
    @PostConstruct
    public void clearRedis() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            // 모든 키 삭제
            redisTemplate.delete(keys);
        }
    }
}
