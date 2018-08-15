package com.cc.springbootredislock.controller;

import com.cc.springbootredislock.service.LockService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
@RestController
public class LockController {

    @Resource
    private LockService lockService;

    @RequestMapping("lock")
    public String lock() {
        String id = UUID.randomUUID().toString();
        lockService.test(id);
        return new String(id);
    }
}
