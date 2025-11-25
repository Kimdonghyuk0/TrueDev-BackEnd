package com.kdh.truedev.config;

import com.kdh.truedev.springSecurity.JwtUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
// 경로/메서드/userId/소요시간을 MDC에 담아 로그로 남긴다.
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "loggingInterceptorStart";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        MDC.put("path", request.getRequestURI());
        MDC.put("method", request.getMethod());
        Long userId = resolveUserId();
        if (userId != null) {
            MDC.put("userId", String.valueOf(userId));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object startedAt = request.getAttribute(START_TIME);
        long duration = startedAt instanceof Long ? (System.currentTimeMillis() - (Long) startedAt) : -1L;
        MDC.put("status", String.valueOf(response.getStatus()));
        if (duration >= 0) {
            MDC.put("durationMs", String.valueOf(duration));
        }

        log.info("REQ {} {} user={} status={} durationMs={}",
                MDC.get("method"),
                MDC.get("path"),
                MDC.get("userId"),
                MDC.get("status"),
                MDC.get("durationMs"));

        MDC.clear();
    }

    private Long resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserDetails jwtUserDetails) {
            return jwtUserDetails.getUserId();
        }
        return null;
    }
}
