package com.cc.springbootrediscache.controller;

import com.cc.springbootrediscache.entity.User;
import com.cc.springbootrediscache.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PutMapping
    public User add(@RequestBody User user) {
        return userService.save(user);
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable Integer id) {
        return userService.delUser(id);
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable Integer id) {
        return userService.findUser(id);
    }

    @PostMapping
    public User update(@RequestBody User user) {
        return userService.save(user);
    }
}
