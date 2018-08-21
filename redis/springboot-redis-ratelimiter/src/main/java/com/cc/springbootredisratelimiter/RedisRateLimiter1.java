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
public class RedisRateLimiter1 {

    static final String BUCKET = "BUCKET";

    // 从redis令牌桶中获取一个令牌
    public static String getTokenFromBucket(Jedis jedis, int limit , long timeout) {
        String identifierString = UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        Transaction transaction = jedis.multi();
        // 1、清空过期信号量
        transaction.zremrangeByScore(BUCKET.getBytes(), "-inf".getBytes(), String.valueOf(now - timeout).getBytes());

        // 2、插入新增信号量
        transaction.zadd(BUCKET, now, identifierString);

        transaction.zrank(BUCKET, identifierString);

        // 3、获取信号量的排名
        List<Object> results = transaction.exec();
        long rank = (Long) results.get(results.size() -1);
        log.info("rank: {}, limit: {}", rank, limit);
        // 4、判断信号量排名，如果这个信号量不在限定的排名内，否则移除
        if (rank < limit) {
            return identifierString;
        } else {
            // 没有获取信号量，删除数据
            transaction.zrem(BUCKET, identifierString);
            transaction.exec();

        }
        return null;
    }
}
