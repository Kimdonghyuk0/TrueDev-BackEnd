package com.week4.lucas.config.springSecurity.dto;


import lombok.Builder;

@Builder
public record TokenDto(
    String grantType,          // "Bearer"
    String accessToken,        // 액세스 토큰
    long accessTokenExpiresIn, // 만료 시간 (epoch millis)
    String refreshToken        // 리프레시 토큰
){}
