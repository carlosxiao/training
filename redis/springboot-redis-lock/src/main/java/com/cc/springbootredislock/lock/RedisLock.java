package com.cc.springbootredislock.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * REDIS KEY
     * @return
     */
    String[] keys() default "";

    /**
     * 过期时间
     * @return
     */
    long expire() default Long.MIN_VALUE;

    /**
     * 尝试获取锁超时时间
     * @return
     */
    long tryTimeout() default Long.MIN_VALUE;
}
