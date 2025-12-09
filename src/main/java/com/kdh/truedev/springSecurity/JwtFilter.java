package com.kdh.truedev.springSecurity;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final String ACCESS_TOKEN_COOKIE = "accessToken";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        // 1. Request Header 에서 토큰을 꺼냄
        String jwt = resolveToken(request);

        // 2. validateToken 으로 토큰 유효성 검사
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            if (log.isDebugEnabled()) {
                log.debug("JWT authenticated: user={}, uri={}", authentication.getName(), request.getRequestURI());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("JWT missing or invalid for uri={}", request.getRequestURI());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            if (log.isDebugEnabled()) log.debug("JWT from Authorization header");
            return bearerToken.substring(7);
        }
        // 헤더에 없으면 쿠키에서 accessToken 찾기
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(c -> ACCESS_TOKEN_COOKIE.equals(c.getName()))
                    .map(c -> {
                        String val = c.getValue();
                        if (StringUtils.hasText(val) && val.startsWith(BEARER_PREFIX)) {
                            return val.substring(BEARER_PREFIX.length());
                        }
                        return val;
                    })
                    .filter(StringUtils::hasText)
                    .findFirst()
                    .map(v -> {
                        if (log.isDebugEnabled()) log.debug("JWT from Cookie");
                        return v;
                    })
                    .orElse(null);
        }
        return null;
    }

}
