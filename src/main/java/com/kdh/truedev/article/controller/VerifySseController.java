package com.kdh.truedev.article.controller;

import com.kdh.truedev.article.verify.SseEmitterRegistry;
import com.kdh.truedev.user.service.UserService;
import com.kdh.truedev.user.support.AuthTokenResolver;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Article-AI", description = "게시글 AI 검증 SSE")
@RestController
@RequiredArgsConstructor
public class VerifySseController {

    private final SseEmitterRegistry registry;
    private final AuthTokenResolver authTokenResolver;
    private static final long TIMEOUT = 30 * 60 * 1000L; // 30분

    @Operation(summary = "AI 검증 SSE 구독")
    @GetMapping(value = "/sse/verify", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe() {
        Long userId = authTokenResolver.resolveUserIdIfPresent();
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        SseEmitter emitter = registry.add(userId, TIMEOUT);
        try {
            emitter.send(SseEmitter.event().name("ping").data("ok"));
        } catch (Exception ignored) {}
        return ResponseEntity.ok(emitter);
    }
}
