package com.cc.springbootredislock.config;

import com.cc.springbootredislock.aspect.RedisLockAspect;
import com.cc.springbootredislock.factory.RedisLockFactory;
import com.cc.springbootredislock.lock.RedisKeyGenerator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Import(RedisLockAspect.class)
public class RedisLockAutoConfiguration {

    @Bean
    public RedisLockFactory redisLockFactory() {
        return new RedisLockFactory();
    }

    @Bean
    public RedisKeyGenerator redisKeyGenerator() {
        return new RedisKeyGenerator();
    }
}
