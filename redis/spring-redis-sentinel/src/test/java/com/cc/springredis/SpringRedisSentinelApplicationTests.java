package com.cc.springredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringRedisSentinelApplication.class)
public class SpringRedisSentinelApplicationTests {

	@Resource
	private RedisUtil redisUtil;

	@Test
	public void testSentinel() {
		redisUtil.set("test", "1111111111");
		System.out.println(redisUtil.get("test"));
	}

}
