package com.kdh.truedev.article.verify;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter add(Long userId, long timeout) {
        SseEmitter emitter = new SseEmitter(timeout);
        emitters.put(userId, emitter);
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((ex) -> emitters.remove(userId));
        return emitter;
    }

    public void sendToUser(Long userId, SseEmitter.SseEventBuilder event) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) return;
        try {
            emitter.send(event);
            emitter.complete();
            emitters.remove(userId);
        } catch (Exception e) {
            emitters.remove(userId);
        }
    }
}
