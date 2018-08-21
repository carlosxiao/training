package com.cc.springbootredisratelimiter;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.ZParams;

import java.util.List;
import java.util.UUID;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
@Slf4j
public class RedisRateLimiter {

    static final String BUCKET = "BUCKET";

    static final String BUCKET_COUNT = "BUCKET_COUNT";

    static final String BUCKET_MONITOR = "BUCKET_MONITOR";

    // 从redis令牌桶中获取一个令牌
    public static String getTokenFromBucket(Jedis jedis, int limit , long timeout) {
        String identifierString = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Transaction transaction = jedis.multi();
        // 1、清空过期信号量
        transaction.zremrangeByScore(BUCKET_MONITOR.getBytes(), "-inf".getBytes(), String.valueOf(now - timeout).getBytes());
        ZParams params = new ZParams();
        params.weightsByDouble(1.0,0.0);
        transaction.zinterstore(BUCKET, params, BUCKET, BUCKET_MONITOR);
        // 自增计数器
        transaction.incr(BUCKET_COUNT);
        List<Object> results = transaction.exec();
        long counter = (Long) results.get(results.size() - 1);
        // 2、插入新增信号量
        transaction = jedis.multi();
        transaction.zadd(BUCKET_MONITOR, now, identifierString);
        transaction.zadd(BUCKET, counter, identifierString);
        transaction.zrank(BUCKET, identifierString);
        // 3、获取信号量的排名
        results = transaction.exec();
        long rank = (Long) results.get(results.size() -1);
        log.info("rank: {}, limit: {}", rank, limit);
        // 4、判断信号量排名，如果这个信号量不在限定的排名内，否则移除
        if (rank < limit) {
            return identifierString;
        } else {
            // 没有获取信号量，删除数据
            transaction = jedis.multi();
            transaction.zrem(BUCKET, identifierString);
            transaction.zrem(BUCKET_MONITOR, identifierString);
            transaction.exec();

        }
        return null;
    }
}
