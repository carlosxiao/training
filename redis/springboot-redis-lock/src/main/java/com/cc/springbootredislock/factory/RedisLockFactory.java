package com.cc.springbootredislock.factory;

import com.cc.springbootredislock.lock.RedisLockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.UUID;

/**
 * Created by CarlosXiao on 2018/7/2.
 */
public class RedisLockFactory {

    @Autowired
    private RedisTemplate redisTemplate;

    // PS: 使用Jedis在cluster是不支持lua脚本的。 EvalSha is not supported in cluster environment.
    // lettuce
    private static final String LUA_SCRIPT_LOCK = "return redis.call('set',KEYS[1],ARGV[1],'NX','PX',ARGV[2])";
    private static final RedisScript<String> SCRIPT_LOCK = new DefaultRedisScript<String>(LUA_SCRIPT_LOCK, String.class);
    private static final String LUA_SCRIPT_UNLOCK = "if redis.call('get',KEYS[1]) == ARGV[1] then return tostring(redis.call('del', KEYS[1])) else return '0' end";
    private static final RedisScript<String> SCRIPT_UNLOCK = new DefaultRedisScript<String>(LUA_SCRIPT_UNLOCK, String.class);


    /**
     * 加锁
     *
     * @param redisKey   缓存KEY
     * @param expire     到期时间 毫秒
     * @param tryTimeout 尝试获取锁超时时间 毫秒
     * @return
     */
    public RedisLockInfo tryLock(String redisKey, long expire, long tryTimeout) {
        Assert.isTrue(tryTimeout > 0, "tryTimeout必须大于0");
        long timestamp = System.currentTimeMillis();
        int tryCount = 0;
        String lockId = UUID.randomUUID().toString();
        while ((System.currentTimeMillis() - timestamp) < tryTimeout) {
            try {
                Object lockResult = redisTemplate.execute(SCRIPT_LOCK,
                        redisTemplate.getStringSerializer(),
                        redisTemplate.getStringSerializer(),
                        Collections.singletonList(redisKey),
                        lockId, String.valueOf(expire));
                tryCount++;
                if (null != lockResult && "OK".equals(lockResult)) {
                    return new RedisLockInfo(lockId, redisKey, expire, tryTimeout, tryCount);
                } else {
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解锁
     *
     * @param redisLockInfo 获取锁返回的对象
     * @return
     */
    public boolean releaseLock(RedisLockInfo redisLockInfo) {
        Object releaseResult = null;
        try {
            releaseResult = redisTemplate.execute(SCRIPT_UNLOCK,
                    redisTemplate.getStringSerializer(),
                    redisTemplate.getStringSerializer(),
                    Collections.singletonList(redisLockInfo.getRedisKey()),
                    redisLockInfo.getLockId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return null != releaseResult && releaseResult.equals(1);
    }
}
