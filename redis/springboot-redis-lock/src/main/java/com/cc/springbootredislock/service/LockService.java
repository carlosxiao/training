package com.cc.springbootredislock.service;

import com.cc.springbootredislock.lock.RedisLock;
import org.springframework.stereotype.Service;

/**
 * Created by CarlosXiao on 2018/7/2.
 */

@Service
public class LockService {

    @RedisLock(keys = "#id", expire = 30000, tryTimeout = 1000)
    public String test(String id) {
        //
        return id;
    }
}
