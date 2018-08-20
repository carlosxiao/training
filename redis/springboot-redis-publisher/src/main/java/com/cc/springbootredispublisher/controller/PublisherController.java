package com.cc.springbootredispublisher.controller;

import com.cc.springbootredispublisher.service.PublishService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
@RestController
@RequestMapping
public class PublisherController {

    @Resource
    private PublishService publishService;

    @GetMapping("/publish/{msg}")
    public String publih(@PathVariable String msg) {
        // 发送消息
        publishService.publish(msg);
        return "ok";
    }
}
