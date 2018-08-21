package com.cc.springbootredisratelimiter.annotation;

import java.lang.annotation.*;

/**
 * Created by CarlosXiao on 2018/7/2.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    int limit() default 10;
    int timeout() default 1000;
}
