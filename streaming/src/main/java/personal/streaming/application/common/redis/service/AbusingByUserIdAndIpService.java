package personal.streaming.application.common.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import personal.streaming.application.common.redis.dto.AbusingKey;
import personal.streaming.application.port.redis.AbusingService;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class AbusingByUserIdAndIpService implements AbusingService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * key = users:2:contentPosts:1:ip:0.0.0.0.1
     */

    @Override
    public boolean isAbusing(AbusingKey key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key.getKey()));
    }

    @Override
    public void setAbusing(AbusingKey key) {
        redisTemplate.opsForValue().set(key.getKey(), "1", 30, TimeUnit.SECONDS);
    }
}
