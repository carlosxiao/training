package com.cc.springredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringRedisClusterApplication.class)
public class SpringRedisClusterApplicationTests {

	@Resource
	private RedisUtil redisUtil;

	@Test
	public void testCluster() {
		redisUtil.set("test", "1111111111");
		System.out.println(redisUtil.get("test"));
	}

}
