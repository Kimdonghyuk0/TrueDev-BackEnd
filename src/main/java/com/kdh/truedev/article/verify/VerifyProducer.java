package com.kdh.truedev.article.verify;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VerifyProducer {

    private final RedisTemplate<String, String> redisTemplate;

    public String publish(Long articleId, Long userId) {
        String jobId = UUID.randomUUID().toString();
        StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
        MapRecord<String, String, String> record = MapRecord.create(
                VerifyQueues.STREAM_KEY,
                Map.of(
                        "jobId", jobId,
                        "articleId", String.valueOf(articleId),
                        "userId", String.valueOf(userId),
                        "retry", "0",
                        "requestedAt", Instant.now().toString()
                )
        );
        ops.add(record);
        return jobId;
    }
}
