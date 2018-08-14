package com.cc.springbootrediscache.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by CarlosXiao on 2018/7/1.
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;

    private String username;

    private String password;

    private Integer status;

    public User(String username, String password, Integer status) {
        this.username = username;
        this.password = password;
        this.status = status;
    }
}
