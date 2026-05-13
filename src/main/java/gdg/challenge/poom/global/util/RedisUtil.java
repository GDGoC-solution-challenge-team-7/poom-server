package gdg.challenge.poom.global.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    public <T> void save(String key, T value, Duration duration){
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public <T> T get(String key, Class<T> clz){
        return clz.cast(redisTemplate.opsForValue().get(key));
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

    public boolean has(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
