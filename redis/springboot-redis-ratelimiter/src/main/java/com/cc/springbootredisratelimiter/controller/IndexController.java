package com.cc.springbootredisratelimiter.controller;

import com.cc.springbootredisratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
@RestController
public class IndexController {

    @Resource
    private JedisPool redisPool;

    @GetMapping("/t1")
    @RateLimiter(limit = 2, timeout = 10000)
    public void t1() {

    }

    @GetMapping("/t2")
    @RateLimiter(limit = 15, timeout = 10000)
    public void t2() {

    }
}
