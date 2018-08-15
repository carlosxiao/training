package com.cc.springbootredislock.lock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by CarlosXiao on 2018/7/2.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisLockInfo {

    /**
     * 锁ID UUID
     */
    private String lockId;

    /**
     * REDIS KEY
     */
    private String redisKey;

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 尝试获取锁超时时间
     */
    private Long tryTimeout;

    /**
     * 尝试获取锁次数
     */
    private int tryCount;
}
