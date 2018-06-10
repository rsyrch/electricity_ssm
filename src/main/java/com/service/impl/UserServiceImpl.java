package com.service.impl;

import com.common.Const;
import com.common.ServerResponse;
import com.dao.UserMapper;
import com.pojo.User;
import com.service.IUserService;
import com.util.MD5Util;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public ServerResponse<String> checkQuestion(String userName, String question, String answer) {
        ServerResponse response = this.checkValue(userName, Const.USER_NAME);
        if(response.isSuccess()) {
            return ServerResponse.createByErrorMessage("用户不存在");
        }
        return null;
    }
}
