package com.cc.springbootrediscache.service;

import com.cc.springbootrediscache.entity.User;
import com.cc.springbootrediscache.mapper.UserMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    /**
     * 添加和更新都调用这个方法
     * @param user
     * @return user_1
     */
    @CachePut(value = "usercache", key = "'user_' + #user.id.toString()", unless = "#result eq null")
    public User save(User user) {
        if (null != user.getId()) {
            userMapper.updateUser(user);
        } else {
            userMapper.addUser(user);
        }
        return user;
    }

    @Cacheable(value = "usercache", key = "'user_' + #id", unless = "#result eq null")
    public User findUser(Integer id) {
        return userMapper.getById(id);
    }


    @CacheEvict(value = "usercache", key = "'user_' + #id", condition = "#result eq true")
    public boolean delUser(Integer id) {
        return userMapper.deleteUserById(id) > 0;
    }
}
