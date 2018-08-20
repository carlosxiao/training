package com.cc.springbootredispublisher.service.impl;

import com.cc.springbootredispublisher.service.PublishService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
@Service
public class PublishServiceImpl implements PublishService{

    static Logger logger = LoggerFactory.getLogger(PublishServiceImpl.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${redis.msg.topic}")
    private String redisTopic;

    @Override
    public void publish(String msg) {
        stringRedisTemplate.convertAndSend(redisTopic, msg);
        logger.info("send msg: {}", msg);
    }
}
