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

    public ServerResponse<User> register(User user) {
        int resultCount = userMapper.checkUserName(user.getUsername());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMessage("用户名存在");
        }
        resultCount = userMapper.checkEmail(user.getEmail());
        if(resultCount > 0) {
            return ServerResponse.createByErrorMessage("邮箱已存在");
        }
        // 设置权限
        user.setRole(Const.Roel.ROLE_CUSTOMER);

        // MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        // 保存
        resultCount = userMapper.insert(user);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");
    }


}
