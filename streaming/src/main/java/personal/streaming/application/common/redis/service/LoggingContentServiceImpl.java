package personal.streaming.application.common.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import personal.streaming.application.port.redis.LoggingContentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class LoggingContentServiceImpl implements LoggingContentService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "logged:time:";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * key = logged:time:20241030
     * value = SortedSet { 1 , 2, 5, 8, 11 } 오름차순 정렬
     */

    @Override
    public void addLog(Long contentPostId) {
        redisTemplate.opsForZSet().add(getKey(), String.valueOf(contentPostId), contentPostId);
    }

    public String getKey() {
        return PREFIX + getTime();
    }

    public String getTime() {
        return LocalDateTime.now().format(formatter);
    }
}
