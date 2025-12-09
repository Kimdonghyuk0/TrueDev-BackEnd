package com.kdh.truedev.article.verify;

import com.kdh.truedev.article.entity.Article;
import com.kdh.truedev.article.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class VerifyConsumer implements InitializingBean {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisConnectionFactory connectionFactory;
    private final ArticleRepository articleRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final SseEmitterRegistry emitterRegistry;
    @Value("${factchecker.url:http://localhost:8000/fact-check}")
    private String factCheckerUrl;

    private Subscription subscription;

    @Override
    public void afterPropertiesSet() {
        // 그룹 생성 시도
        try {
            redisTemplate.opsForStream().createGroup(VerifyQueues.STREAM_KEY, ReadOffset.latest(), VerifyQueues.GROUP);
        } catch (Exception ignored) {
        }
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .build();
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        subscription = container.receive(
                Consumer.from(VerifyQueues.GROUP, "worker-1"),
                StreamOffset.create(VerifyQueues.STREAM_KEY, ReadOffset.lastConsumed()),
                this::onMessage
        );
        container.start();
    }

    private void onMessage(MapRecord<String, String, String> message) {
        Map<String, String> v = message.getValue();
        Long articleId = parseLong(v.get("articleId"));
        Long userId = parseLong(v.get("userId"));
        String jobId = v.get("jobId");
        if (articleId == null || userId == null || jobId == null) {
            ack(message);
            return;
        }
        try {
            process(jobId, articleId, userId);
            ack(message);
        } catch (Exception e) {
            log.error("verify job failed jobId={} articleId={}", jobId, articleId, e);
            ack(message);
        }
    }

    @Transactional
    public void process(String jobId, Long articleId, Long userId) {
        Optional<Article> opt = articleRepository.findById(articleId);
        if (opt.isEmpty()) return;
        Article article = opt.get();
        if (!Objects.equals(article.getUser().getId(), userId) || Boolean.TRUE.equals(article.getIsDeleted())) {
            return;
        }
        String text = "제목: " + article.getTitle() + "\n내용: " + article.getContent();
        boolean isFact = false;
        String aiComment = "";
        try {
            HttpEntity<Map<String, String>> req = new HttpEntity<>(Map.of("text", text));
            ResponseEntity<Map> res = restTemplate.exchange(
                    factCheckerUrl,
                    HttpMethod.POST,
                    req,
                    Map.class
            );
            Map body = res.getBody();
            if (body != null) {
                Object isFactObj = body.get("isFact");
                Object aiCommentObj = body.get("aiComment");
                isFact = Boolean.TRUE.equals(isFactObj);
                aiComment = aiCommentObj != null ? aiCommentObj.toString() : "";
            }
        } catch (Exception e) {
            log.error("FastAPI call failed jobId={}", jobId, e);
        }
        article.setVerified(isFact);
        article.setCheck(true);
        article.setAiMessage(aiComment);
        articleRepository.save(article);

        // SSE 알림
        emitterRegistry.sendToUser(userId, SseEmitter.event()
                .name("verify-done")
                .data(Map.of(
                        "jobId", jobId,
                        "articleId", articleId,
                        "isVerified", isFact,
                        "aiMessage", aiComment
                )));
    }

    private void ack(MapRecord<String, String, String> message) {
        try {
            redisTemplate.opsForStream().acknowledge(VerifyQueues.GROUP, message);
        } catch (Exception e) {
            log.warn("ack failed {}", message.getId(), e);
        }
    }

    private Long parseLong(String v) {
        try {
            return v == null ? null : Long.parseLong(v);
        } catch (Exception e) {
            return null;
        }
    }
}
