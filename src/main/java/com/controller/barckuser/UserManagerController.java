package com.controller.barckuser;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * @FunctionName: UserManagerController
 * @Name: luo chuan
 * @Date: 2018/6/11 22:56
 * @Description:  后台用户管理
 */
@Controller
@RequestMapping(value = "/manage/user")
public class UserManagerController {

    @Autowired
    private IUserService iUserService;

    /**
     * Name: login
     * Description: 后台管理员登陆
     * Author: luo chuan
     * CreateDate: 2018/6/11 23:01
    */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    public ServerResponse<User> login(String userName, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(userName, password);
        if(response.isSuccess()) {
            User user = response.getData();
            if(user.getRole() == Const.Roel.ROLE_ADMIN) {
                // 管理员登陆
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }
            else {
                return ServerResponse.createByErrorMessage("非管理员，禁止登陆");
            }
        }
        return response;
    }
}
