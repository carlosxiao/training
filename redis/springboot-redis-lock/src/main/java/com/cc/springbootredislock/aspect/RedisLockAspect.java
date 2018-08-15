package com.cc.springbootredislock.aspect;

import com.cc.springbootredislock.factory.RedisLockFactory;
import com.cc.springbootredislock.lock.RedisKeyGenerator;
import com.cc.springbootredislock.lock.RedisLock;
import com.cc.springbootredislock.lock.RedisLockInfo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
@Aspect
@Component
@Slf4j
public class RedisLockAspect {
    @Autowired
    private RedisLockFactory redisLockFactory;
    @Autowired
    private RedisKeyGenerator redisKeyGenerator;


    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint point, RedisLock redisLock) throws Throwable {
        RedisLockInfo redisLockInfo = null;
        try {
            String keyName = redisKeyGenerator.getKeyName(point, redisLock);
            redisLockInfo = redisLockFactory.tryLock(keyName, redisLock.expire(), redisLock.tryTimeout());
            if (null != redisLockInfo) {
                Object result = point.proceed();
                return result;
            }
        } catch (Throwable e) {
            log.error("around exception", e);
            throw e;
        } finally {
            if (null != redisLockInfo) {
                redisLockFactory.releaseLock(redisLockInfo);
            }
        }
        return null;
    }
}
