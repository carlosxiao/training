package com.cc.springbootredislottery;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisLotteryApplication.class)
@Slf4j
public class SpringbootRedisLotteryApplicationTests {

    @Test
    public void contextLoads() {
        int count = 100;
        for (int i = 1; i <= count; i++) {
            // 1 0001,
            log.info("num: {}", String.format("%0" + String.valueOf(count).length() + "d", i));
        }
    }
}