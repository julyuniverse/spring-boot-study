package com.springbootstudy.sociallogin.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Lee Taesung
 * @since 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisConnectionFactory redisConnectionFactory;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public void setData(String key, String value, Long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public String getData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * @author Lee Taesung
     * @since 1.0
     */
    public void deleteData(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 키를 통해 조회를 할 때 keys 명령어가 아닌 scan 명령어로 조회한다.
     *
     * @param keyPattern 검색어
     * @author Lee Taesung
     * @since 1.0
     */
    public List<String> scanData(String keyPattern) {
        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        KeyScanOptions options = (KeyScanOptions) KeyScanOptions.scanOptions().match(keyPattern).build();
        List<String> dataList = new ArrayList<>();
        Cursor<byte[]> cursor = redisConnection.scan(options);
        while (cursor.hasNext()) {
            String data = new String(cursor.next());
            dataList.add(data);
        }

        return dataList;
    }
}
