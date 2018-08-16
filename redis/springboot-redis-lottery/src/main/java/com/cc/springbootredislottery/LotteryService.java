package com.cc.springbootredislottery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by CarlosXiao on 2018/7/5.
 */

@Service
@Slf4j
public class LotteryService {

    public static final String LOTTERY_POOL = "LOTTERY_POOL";

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 初始化抽奖号码
     * @param count
     */
    public void initLottery(int count) {
        // 0-count list > shuffle > redis list
        List<String> numList = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            // 1 0001,
            numList.add(String.format("%0"+ String.valueOf(count).length() +"d", i));
        }
        // 随机打乱顺序
        Collections.shuffle(numList);
        // 存入redis
        Long result = redisTemplate.opsForList().leftPushAll(LOTTERY_POOL, numList);
        log.info("add result : {}", result);
    }

    public String getLottery() {
        String num = String.valueOf(redisTemplate.opsForList().rightPop(LOTTERY_POOL));
        if (StringUtils.isEmpty(num)) {
            throw new RuntimeException("sold out");
        }
        return num;
    }
}
