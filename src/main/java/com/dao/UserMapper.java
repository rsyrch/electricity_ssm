package com.dao;

import com.pojo.User;
import org.apache.ibatis.annotations.Param;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    // 检查用户是否存在
    int checkUserName(String userName);

    // 用户登陆
    User userLongin(@Param("userName") String userName, @Param("password") String password);

    // 检查邮箱
    int checkEmail(String email);

    // 获取修改密码问题
    String getPasswordQuestion(String userName);
}