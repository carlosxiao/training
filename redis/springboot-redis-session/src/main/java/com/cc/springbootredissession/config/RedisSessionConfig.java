package com.cc.springbootredissession.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
@Configuration
@EnableRedisHttpSession
public class RedisSessionConfig {
}
