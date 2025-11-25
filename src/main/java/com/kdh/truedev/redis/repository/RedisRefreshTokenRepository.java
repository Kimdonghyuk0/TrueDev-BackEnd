package com.kdh.truedev.redis.repository;

import com.kdh.truedev.redis.entity.RedisRefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RedisRefreshTokenRepository extends CrudRepository<RedisRefreshToken, Long> {
}
