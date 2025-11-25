package com.kdh.truedev.redis.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class RedisProperties {
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.host}")
    private String host;

}
