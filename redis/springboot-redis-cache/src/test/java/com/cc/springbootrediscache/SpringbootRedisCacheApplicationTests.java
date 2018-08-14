package com.cc.springbootrediscache;

import com.cc.springbootrediscache.entity.User;
import com.cc.springbootrediscache.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.logging.Logger;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringbootRedisCacheApplication.class)
@Slf4j
public class SpringbootRedisCacheApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Test
    public void testAdd() {
        User user = new User("cc", "123456", 1);
        userMapper.addUser(user);
        Assert.assertNotNull(user.getId());
    }

    @Test
    public void testList() {
        List<User> userList = userMapper.queryUserList();
        log.info("user list: {}", userList.size());
    }

}