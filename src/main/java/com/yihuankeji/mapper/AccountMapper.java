package com.yihuankeji.mapper;

import com.yihuankeji.pojo.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface AccountMapper {
    // 查询用户名是否已存在
    @Select("SELECT COUNT(*) FROM `user` WHERE username = #{username}")
    Integer checkUsernameExist(@Param("username") String username);

    // 注册用户，返回受影响行数，并回填自增ID到 user.id
    @Insert("INSERT INTO `user` (username, password, name, registerTime) VALUES (#{username}, #{password}, #{name}, #{registerTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int register(User user);

    // 根据用户名查询用户信息
    @Select("SELECT id, username, password, name, tel, registerTime, avatar FROM `user` WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    // 更新用户密码
    @Update("UPDATE `user` SET password = #{newPassword} WHERE username = #{username}")
    int updatePassword(@Param("username") String username, @Param("newPassword") String newPassword);


}
