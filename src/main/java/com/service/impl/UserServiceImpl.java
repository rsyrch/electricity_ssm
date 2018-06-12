package com.service.impl;

import com.common.Const;
import com.common.ServerResponse;
import com.common.TokenCache;
import com.dao.UserMapper;
import com.pojo.User;
import com.service.IUserService;
import com.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/9 15:05
 * @Description:
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * Name: login
     * Description: 登陆
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:15
    */
    public ServerResponse<User> login(String userName, String password) {
        int resultCount = userMapper.checkUserName(userName);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }

        User user = userMapper.userLongin(userName, MD5Util.MD5EncodeUtf8(password));
        if(user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    /**
     * Name: register
     * Description: 注册
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:15
    */
    public ServerResponse<String> register(User user) {

        ServerResponse response = this.checkValue(user.getUsername(), Const.USER_NAME);
        if(!response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户名已存在");
        }

        response = this.checkValue(user.getEmail(), Const.EMAIL);
        if(!response.isSuccess()) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        // 设置权限
        user.setRole(Const.Roel.ROLE_CUSTOMER);

        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        // 保存
        int resultCount = userMapper.insert(user);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }

    /**
     * Name: checkValue
     * Description: 检查用户名和email是否有效
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:20
    */
    public ServerResponse<String> checkValue(String str, String type) {
        if(StringUtils.isNotBlank(type)) {
            if(type.equals(Const.USER_NAME)) {
                int resultCount = userMapper.checkUserName(str);
                if(resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            }
            if(type.equals(Const.EMAIL)) {
                int resultCount = userMapper.checkEmail(str);
                if(resultCount > 0) {
                    return ServerResponse.createByErrorMessage("邮箱已存在");
                }
            }
        }
        else {
            ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccess();
    }
    
    /**
     * Name: getPasswordPrompt
     * Description: 获取密码提示问题
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:49
    */
    public ServerResponse<String> getPasswordPrompt(String userName) {
        ServerResponse response = this.checkValue(userName, Const.USER_NAME);
        if(response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String passwordQuestion = userMapper.getPasswordQuestion(userName);
        if(StringUtils.isNotBlank(passwordQuestion)) {
            return ServerResponse.createBySuccess(passwordQuestion);
        }
        return ServerResponse.createByErrorMessage("修改密码问题为空");
    }

    /**
     * Name: checkQuestion
     * Description: 检查问题答案
     * Author: luo chuan
     * CreateDate: 2018/6/10 20:20
    */
    public ServerResponse<String> checkQuestion(String userName, String question, String answer) {
        int resultCount = userMapper.checkAnswer(userName, question, answer);
        if(resultCount > 0) {
            String tocken = UUID.randomUUID().toString();
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + userName, tocken);
            return ServerResponse.createBySuccess(tocken);
        }
        return ServerResponse.createByErrorMessage("答案错误");
    }
    
    /**
     * Name: resetPassword
     * Description: 重置密码
     * Author: luo chuan
     * CreateDate: 2018/6/10 20:26
    */
    public ServerResponse<String> resetPassword(String userName, String newPassword, String token) {
        if(StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("参数为空");
        }
        ServerResponse response = this.checkValue(userName, Const.USER_NAME);
        if(response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String thisToken = TokenCache.getKey(TokenCache.TOKEN_PREFIX + userName);
        if(StringUtils.isBlank(thisToken)) {
            return ServerResponse.createByErrorMessage("token无效或者过期");
        }
        if(StringUtils.equals(token, thisToken)) {
            // 重置密码
            String md5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int rowCount = userMapper.uodatePassword(userName, md5Password);
            if(rowCount > 0) {
                return ServerResponse.createBySuccessMessage("密码重置成功");
            }
        }
        else {
            return ServerResponse.createByErrorMessage("token错误");
        }
        return ServerResponse.createByErrorMessage("修改密码失败");
    }
    
    /**
     * Name: loginResetPassword
     * Description: 登陆状态重置密码
     * Author: luo chuan
     * CreateDate: 2018/6/10 21:03
    */
    public ServerResponse<String> loginResetPassword(String oldPassword, String newPassword, User user) {
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword), user.getId());
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("原密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");
    }

    /**
     * Name: updateUserInfo
     * Description: 更新用户信息
     * Author: luo chuan
     * CreateDate: 2018/6/11 14:51
    */
    public ServerResponse<User> updateUserInfo(User user) {
        // userName不能跟新
        int resultCount = userMapper.checkEmailById(user.getId(),user.getEmail());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMessage("此邮箱已经被注册");
        }
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setEmail(user.getEmail());
        newUser.setPhone(user.getPhone());
        newUser.setQuestion(user.getQuestion());
        newUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(newUser);
        if(updateCount > 0) {
            ServerResponse.createBySuccess("更新个人信息成功", newUser);
        }
        return ServerResponse.createByErrorMessage("更新失败");
    }

    /**
     * Name: getUserInfoById
     * Description: 根据用户id获取用户信息
     * Author: luo chuan
     * CreateDate: 2018/6/11 15:52
    */
    public ServerResponse<User> getUserInfoById(Integer userId) {
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null) {
            return ServerResponse.createByErrorMessage("获取用户信息失败");
        }
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);
    }
    
    /**
     * Name: checkUserIsAdmin
     * Description: 检查用户是否是管理员
     * Author: luo chuan
     * CreateDate: 2018/6/12 21:01
    */
    public ServerResponse<String> checkUserIsAdmin(User user) {
        if(user != null && user.getRole().intValue() == Const.Roel.ROLE_ADMIN) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
