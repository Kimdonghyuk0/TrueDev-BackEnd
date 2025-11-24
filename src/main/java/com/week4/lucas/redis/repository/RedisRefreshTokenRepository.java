package com.week4.lucas.redis.repository;

import com.week4.lucas.redis.entity.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisRefreshTokenRepository extends CrudRepository<RedisRefreshToken, Long> {
}
