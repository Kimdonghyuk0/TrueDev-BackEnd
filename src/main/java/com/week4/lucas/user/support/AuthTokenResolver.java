package com.week4.lucas.user.support;

import com.week4.lucas.config.springSecurity.InvalidRefreshTokenException;
import com.week4.lucas.config.springSecurity.JwtUserDetails;
import com.week4.lucas.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class AuthTokenResolver {

    private static final String BEARER_PREFIX = "Bearer ";

    public Long requireUserId() {
        return resolveUserIdInternal().orElseThrow(UserService.UnauthorizedException::new);
    }

    public Long resolveUserIdIfPresent() {
        return resolveUserIdInternal().orElse(null);
    }

    private Optional<Long> resolveUserIdInternal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof JwtUserDetails userDetails) {
            return Optional.ofNullable(userDetails.getUserId());
        }
        return Optional.empty();
    }

    public String resolveRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken) || !refreshToken.startsWith(BEARER_PREFIX)) {
            throw new InvalidRefreshTokenException("Invalid_RefreshToken");
        }
        return refreshToken.substring(BEARER_PREFIX.length());
    }
}
