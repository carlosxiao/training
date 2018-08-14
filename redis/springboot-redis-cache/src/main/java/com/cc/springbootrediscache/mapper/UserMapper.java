package com.cc.springbootrediscache.mapper;

import com.cc.springbootrediscache.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by CarlosXiao on 2018/7/1.
 */
public interface UserMapper {

    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into user (username,password,status) values (#{username}, #{password}, #{status})")
    Integer addUser(User user);

    @Delete("delete from user where id=#{0}")
    Integer deleteUserById(Integer id);

    @Update("update user set username=#{username}, password=#{password}, status=#{status}")
    Integer updateUser(User user);

    @Select("select * from user where id=#{0}")
    User getById(Integer id);

    @Select("select * from user")
    List<User> queryUserList();


}
