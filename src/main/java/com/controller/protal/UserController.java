package com.controller.protal;

import com.common.Const;
import com.common.ServerResponse;
import com.pojo.User;
import com.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;


/**
 * @FunctionName:
 * @Name: luo chuan
 * @Date: 2018/6/9 14:56
 * @Description:
 */
@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    IUserService iUserService;

    /**
     * Name: login
     * Description: 用户登陆
     * Author: luo chuan
     * CreateDate: 2018/6/9 14:58
    */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> userLogin(String userName, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(userName, password);
        if(response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }

    /**
     * Name: ServerResponse
     * Description: 用户退出
     * Author: luo chuan
     * CreateDate: 2018/6/9 17:20
    */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> userLogout(HttpSession session) {
        // 移除session
        session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    /**
     * Name: userRegister
     * Description: 用户注册
     * Author: luo chuan
     * CreateDate: 2018/6/9 17:34
    */
    @RequestMapping(value = "userRegister.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> userRegister(User user) {
        return iUserService.register(user);
    }

    /**
     * Name: checkValue
     * Description: 检查用户名和Email是否有效
     * Author: luo chuan
     * CreateDate: 2018/6/10 0:53
    */
    @RequestMapping(value = "checkValue.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValue(String str, String type) {
        return iUserService.checkValue(str, type);
    }

    /**
     * Name: getUserInfo
     * Description: 从session获取用户信息
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:26
    */
    @RequestMapping(value = "getUserInfoBySession.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfoBySession(HttpSession session) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录，无法获取信息");
        }
        return ServerResponse.createBySuccess(user);
    }

    /**
     * Name: getPasswordPrompt
     * Description: 获取密码提示
     * Author: luo chuan
     * CreateDate: 2018/6/10 1:43
    */
    @RequestMapping(value = "getPasswordPrompt.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> getPasswordPrompt(String userName) {
        return iUserService.getPasswordPrompt(userName);
    }
    
    /**
     * Name: checkQuestion
     * Description: 判断用户的answer
     * Author: luo chuan
     * CreateDate: 2018/6/10 20:14
    */
    @RequestMapping(value = "checkQuestion.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkQuestion(String userName, String question, String answer) {
        return iUserService.checkQuestion(userName, question, answer);
    }

    /**
     * Name: resetPassword
     * Description: 重置密码
     * Author: luo chuan
     * CreateDate: 2018/6/10 20:25
    */
    @RequestMapping(value = "resetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> resetPassword(String userName, String newPassword, String token) {
        return iUserService.resetPassword(userName, newPassword, token);
    }
    
    /**
     * Name: loginResetPassword
     * Description: 登陆重置密码
     * Author: luo chuan
     * CreateDate: 2018/6/10 20:59
    */
    @RequestMapping(value = "loginResetPassword.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> loginResetPassword(HttpSession session, String oldPassword, String newPassword) {
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.loginResetPassword(oldPassword, newPassword, user);
    }

    /**
     * Name: updateUserInfo
     * Description: 更新用户信息
     * Author: luo chuan
     * CreateDate: 2018/6/11 14:46
    */
    @RequestMapping(value = "updateUserInfo.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateUserInfo(HttpSession session, User user) {
        User userSession = (User) session.getAttribute(Const.CURRENT_USER);
        if(userSession == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        // 保存当前用户id
        user.setId(userSession.getId());
        user.setUsername(userSession.getUsername());
        ServerResponse<User> response = iUserService.updateUserInfo(user);
        if(response.isSuccess()) {
            // 更新session
            session.setAttribute(Const.CURRENT_USER,response.getData());
        }
        return response;
    }
    
    /**
     * Name: getUserInfo
     * Description: 获取用户信息
     * Author: luo chuan
     * CreateDate: 2018/6/11 15:46
    */
    @RequestMapping(value = "getUserInformation.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInformation(HttpSession session) {
        User userSession = (User) session.getAttribute(Const.CURRENT_USER);
        if(userSession == null) {
            return ServerResponse.createByErrorMessage("用户未登录");
        }
        return iUserService.getUserInfoById(userSession.getId());
    }
}
